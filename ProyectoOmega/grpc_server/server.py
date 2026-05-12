from concurrent import futures
from pathlib import Path

import grpc

import turbomessage_pb2
import turbomessage_pb2_grpc
from storage import TurboMessageStorage


BASE_DIR = Path(__file__).resolve().parent.parent
DATABASE_PATH = BASE_DIR / "data" / "turbomessage.db"


class TurboMessageServer(turbomessage_pb2_grpc.TurboMessageServicer):
    storage = TurboMessageStorage(str(DATABASE_PATH))

    def createUser(self, request, context):
        created = self.storage.create_user(request.username, request.password)
        return turbomessage_pb2.Status(success=created)

    def userExists(self, request, context):
        exists = self.storage.user_exists(request.username, request.password)
        return turbomessage_pb2.Status(success=exists)

    def sendMail(self, request, context):
        result = self.storage.send_mail(
            request.sender,
            request.receiver,
            request.subject,
            request.message,
        )
        if result == "ok":
            return turbomessage_pb2.Status(success=True)
        if result == "inbox_full":
            context.set_code(grpc.StatusCode.RESOURCE_EXHAUSTED)
            context.set_details("La bandeja de entrada del receptor esta llena.")
        return turbomessage_pb2.Status(success=False)

    def mailInRead(self, request, context):
        updated = self.storage.mark_inbox_mail_as_read(request.receiver, request.id)
        return turbomessage_pb2.Status(success=updated)

    def mailOutRead(self, request, context):
        actualizado = self.storage.mark_outbox_mail_as_read(request.sender, request.id)
        return turbomessage_pb2.Status(success=actualizado)

    def deleteMailIn(self, request, context):
        deleted = self.storage.delete_inbox_mail(request.receiver, request.id)
        return turbomessage_pb2.Status(success=deleted)

    def deleteMailOut(self, request, context):
        deleted = self.storage.delete_outbox_mail(request.sender, request.id)
        return turbomessage_pb2.Status(success=deleted)

    def readMailIn(self, request, context):
        for mail in self.storage.list_inbox(request.username):
            yield turbomessage_pb2.Mail(
                id=mail["id"],
                sender=mail["sender"],
                receiver=mail["receiver"],
                subject=mail["subject"],
                message=mail["message"],
                read=bool(mail["is_read"]),
            )

    def readMailOut(self, request, context):
        for mail in self.storage.list_outbox(request.username):
            yield turbomessage_pb2.Mail(
                id=mail["id"],
                sender=mail["sender"],
                receiver=mail["receiver"],
                subject=mail["subject"],
                message=mail["message"],
                read=bool(mail["is_read"]),
            )


def ofrece_servicio():
    puerto = "65065"
    servidor = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    turbomessage_pb2_grpc.add_TurboMessageServicer_to_server(TurboMessageServer(), servidor)
    servidor.add_insecure_port("[::]:" + puerto)
    servidor.start()
    print("TurboMessage gRPC Server iniciado en el puerto " + puerto)
    servidor.wait_for_termination()


if __name__ == "__main__":
    print("Servicio de TurboMessage Desplegado")
    ofrece_servicio()
