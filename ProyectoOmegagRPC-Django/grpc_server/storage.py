import os
import sqlite3
import threading


class TurboMessageStorage:
    MAX_MAILS = 5

    def __init__(self, db_path):
        self.db_path = db_path
        self.lock = threading.Lock()
        self._ensure_database()

    def _connect(self):
        conexion = sqlite3.connect(self.db_path, check_same_thread=False)
        conexion.row_factory = sqlite3.Row
        return conexion

    def _ensure_database(self):
        os.makedirs(os.path.dirname(self.db_path), exist_ok=True)
        with self._connect() as conexion:
            conexion.execute(
                """
                CREATE TABLE IF NOT EXISTS users (
                    username TEXT PRIMARY KEY,
                    password TEXT NOT NULL
                )
                """
            )
            conexion.execute(
                """
                CREATE TABLE IF NOT EXISTS mails (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sender TEXT NOT NULL,
                    receiver TEXT NOT NULL,
                    subject TEXT NOT NULL,
                    message TEXT NOT NULL,
                    is_read INTEGER NOT NULL DEFAULT 0,
                    deleted_inbox INTEGER NOT NULL DEFAULT 0,
                    deleted_outbox INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(sender) REFERENCES users(username),
                    FOREIGN KEY(receiver) REFERENCES users(username)
                )
                """
            )
            conexion.commit()

    def create_user(self, username, password):
        with self.lock:
            with self._connect() as conexion:
                existente = conexion.execute(
                    "SELECT username FROM users WHERE username = ?",
                    (username,),
                ).fetchone()
                if existente is not None:
                    return False
                conexion.execute(
                    "INSERT INTO users(username, password) VALUES(?, ?)",
                    (username, password),
                )
                conexion.commit()
                return True

    def user_exists(self, username, password):
        with self._connect() as conexion:
            fila = conexion.execute(
                "SELECT username FROM users WHERE username = ? AND password = ?",
                (username, password),
            ).fetchone()
            return fila is not None

    def send_mail(self, sender, receiver, subject, message):
        with self.lock:
            with self._connect() as conexion:
                receptor = conexion.execute(
                    "SELECT username FROM users WHERE username = ?",
                    (receiver,),
                ).fetchone()
                emisor = conexion.execute(
                    "SELECT username FROM users WHERE username = ?",
                    (sender,),
                ).fetchone()
                if receptor is None or emisor is None:
                    return "no_user"

                conteo_inbox = conexion.execute(
                    "SELECT COUNT(*) FROM mails WHERE receiver = ? AND deleted_inbox = 0",
                    (receiver,),
                ).fetchone()[0]
                conteo_outbox = conexion.execute(
                    "SELECT COUNT(*) FROM mails WHERE sender = ? AND deleted_outbox = 0",
                    (sender,),
                ).fetchone()[0]

                if conteo_inbox >= self.MAX_MAILS:
                    return "inbox_full"
                if conteo_outbox >= self.MAX_MAILS:
                    return "outbox_full"

                conexion.execute(
                    "INSERT INTO mails(sender, receiver, subject, message, is_read) VALUES(?, ?, ?, ?, 0)",
                    (sender, receiver, subject, message),
                )
                conexion.commit()
                return "ok"

    def list_inbox(self, username):
        with self._connect() as conexion:
            return conexion.execute(
                """
                SELECT id, sender, receiver, subject, message, is_read
                FROM mails
                WHERE receiver = ? AND deleted_inbox = 0
                ORDER BY id DESC
                """,
                (username,),
            ).fetchall()

    def list_outbox(self, username):
        with self._connect() as conexion:
            return conexion.execute(
                """
                SELECT id, sender, receiver, subject, message, is_read
                FROM mails
                WHERE sender = ? AND deleted_outbox = 0
                ORDER BY id DESC
                """,
                (username,),
            ).fetchall()

    def mark_inbox_mail_as_read(self, receiver, mail_id):
        with self.lock:
            with self._connect() as conexion:
                resultado = conexion.execute(
                    "UPDATE mails SET is_read = 1 WHERE id = ? AND receiver = ? AND deleted_inbox = 0",
                    (mail_id, receiver),
                )
                conexion.commit()
                return resultado.rowcount > 0

    def mark_outbox_mail_as_read(self, sender, mail_id):
        with self.lock:
            with self._connect() as conexion:
                resultado = conexion.execute(
                    "UPDATE mails SET is_read = 1 WHERE id = ? AND sender = ? AND deleted_outbox = 0",
                    (mail_id, sender),
                )
                conexion.commit()
                return resultado.rowcount > 0

    def delete_inbox_mail(self, receiver, mail_id):
        with self.lock:
            with self._connect() as conexion:
                resultado = conexion.execute(
                    "UPDATE mails SET deleted_inbox = 1 WHERE id = ? AND receiver = ? AND deleted_inbox = 0",
                    (mail_id, receiver),
                )
                conexion.commit()
                return resultado.rowcount > 0

    def delete_outbox_mail(self, sender, mail_id):
        with self.lock:
            with self._connect() as conexion:
                resultado = conexion.execute(
                    "UPDATE mails SET deleted_outbox = 1 WHERE id = ? AND sender = ? AND deleted_outbox = 0",
                    (mail_id, sender),
                )
                conexion.commit()
                return resultado.rowcount > 0
