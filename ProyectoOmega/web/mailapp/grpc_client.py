import sys
from pathlib import Path

import grpc


GRPC_DIR = Path(__file__).resolve().parents[2] / "grpc_server"
if str(GRPC_DIR) not in sys.path:
    sys.path.append(str(GRPC_DIR))

import turbomessage_pb2
import turbomessage_pb2_grpc


puerto = "65065"


def register_user(username, password):
    with grpc.insecure_channel("Localhost:" + puerto) as channel:
        stub = turbomessage_pb2_grpc.TurboMessageStub(channel)
        respuesta = stub.createUser(turbomessage_pb2.User(username=username, password=password))
        return respuesta.success


def login_user(username, password):
    with grpc.insecure_channel("Localhost:" + puerto) as channel:
        stub = turbomessage_pb2_grpc.TurboMessageStub(channel)
        respuesta = stub.userExists(turbomessage_pb2.User(username=username, password=password))
        return respuesta.success


def list_inbox(username):
    with grpc.insecure_channel("Localhost:" + puerto) as channel:
        stub = turbomessage_pb2_grpc.TurboMessageStub(channel)
        return list(stub.readMailIn(turbomessage_pb2.User(username=username)))


def list_outbox(username):
    with grpc.insecure_channel("Localhost:" + puerto) as channel:
        stub = turbomessage_pb2_grpc.TurboMessageStub(channel)
        return list(stub.readMailOut(turbomessage_pb2.User(username=username)))


def send_mail(sender, receiver, subject, message):
    with grpc.insecure_channel("Localhost:" + puerto) as channel:
        stub = turbomessage_pb2_grpc.TurboMessageStub(channel)
        try:
            respuesta = stub.sendMail(turbomessage_pb2.Mail(sender=sender, receiver=receiver, subject=subject, message=message))
            return respuesta.success, None
        except grpc.RpcError as e:
            return False, e.details()


def mark_inbox_mail_as_read(mail_id, receiver):
    with grpc.insecure_channel("Localhost:" + puerto) as channel:
        stub = turbomessage_pb2_grpc.TurboMessageStub(channel)
        respuesta = stub.mailInRead(turbomessage_pb2.Mail(id=mail_id, receiver=receiver))
        return respuesta.success


def mark_outbox_mail_as_read(mail_id, sender):
    with grpc.insecure_channel("Localhost:" + puerto) as channel:
        stub = turbomessage_pb2_grpc.TurboMessageStub(channel)
        respuesta = stub.mailOutRead(turbomessage_pb2.Mail(id=mail_id, sender=sender))
        return respuesta.success


def delete_inbox_mail(mail_id, receiver):
    with grpc.insecure_channel("Localhost:" + puerto) as channel:
        stub = turbomessage_pb2_grpc.TurboMessageStub(channel)
        respuesta = stub.deleteMailIn(turbomessage_pb2.Mail(id=mail_id, receiver=receiver))
        return respuesta.success


def delete_outbox_mail(mail_id, sender):
    with grpc.insecure_channel("Localhost:" + puerto) as channel:
        stub = turbomessage_pb2_grpc.TurboMessageStub(channel)
        respuesta = stub.deleteMailOut(turbomessage_pb2.Mail(id=mail_id, sender=sender))
        return respuesta.success
