from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.urls import reverse

from . import grpc_client


def _get_logged_user(request):
    return request.session.get("username")


def _require_login(request):
    if _get_logged_user(request) is None:
        return HttpResponseRedirect(reverse("mailapp:login"))
    return None


def login_view(request):
    if request.method == "POST":
        username = request.POST.get("username", "").strip()
        password = request.POST.get("password", "").strip()
        if grpc_client.login_user(username, password):
            request.session["username"] = username
            return HttpResponseRedirect(reverse("mailapp:inbox"))
        return render(
            request,
            "mailapp/login.html",
            {"error": "No se pudo iniciar sesion."},
        )
    return render(request, "mailapp/login.html")


def register_view(request):
    if request.method == "POST":
        username = request.POST.get("username", "").strip()
        password = request.POST.get("password", "").strip()
        if grpc_client.register_user(username, password):
            return render(
                request,
                "mailapp/register.html",
                {"success": "Usuario registrado exitosamente."},
            )
        return render(
            request,
            "mailapp/register.html",
            {"error": "No se pudo registrar al usuario."},
        )
    return render(request, "mailapp/register.html")


def logout_view(request):
    request.session.flush()
    return HttpResponseRedirect(reverse("mailapp:login"))


def inbox_view(request):
    redirect = _require_login(request)
    if redirect is not None:
        return redirect

    username = _get_logged_user(request)
    mails = grpc_client.list_inbox(username)
    return render(
        request,
        "mailapp/inbox.html",
        {"username": username, "mails": mails},
    )


def outbox_view(request):
    redirect = _require_login(request)
    if redirect is not None:
        return redirect

    username = _get_logged_user(request)
    mails = grpc_client.list_outbox(username)
    return render(
        request,
        "mailapp/outbox.html",
        {"username": username, "mails": mails},
    )


def compose_view(request):
    redirect = _require_login(request)
    if redirect is not None:
        return redirect

    username = _get_logged_user(request)
    if request.method == "POST":
        receiver = request.POST.get("receiver", "").strip()
        subject = request.POST.get("subject", "").strip()
        message = request.POST.get("message", "").strip()
        sent, error_msg = grpc_client.send_mail(username, receiver, subject, message)
        contexto = {"username": username}
        if sent:
            contexto["success"] = "Correo enviado exitosamente."
        else:
            contexto["error"] = error_msg if error_msg else "No se pudo enviar el correo."
        return render(request, "mailapp/compose.html", contexto)

    return render(request, "mailapp/compose.html", {"username": username})


def inbox_detail_view(request, mail_id):
    redirect = _require_login(request)
    if redirect is not None:
        return redirect

    username = _get_logged_user(request)
    grpc_client.mark_inbox_mail_as_read(mail_id, username)
    mails = grpc_client.list_inbox(username)
    selected_mail = None
    for mail in mails:
        if mail.id == mail_id:
            selected_mail = mail
            break

    if selected_mail is None:
        return HttpResponseRedirect(reverse("mailapp:inbox"))

    return render(
        request,
        "mailapp/mail_detail.html",
        {"mail": selected_mail, "box": "inbox"},
    )


def outbox_detail_view(request, mail_id):
    redirect = _require_login(request)
    if redirect is not None:
        return redirect

    username = _get_logged_user(request)
    grpc_client.mark_outbox_mail_as_read(mail_id, username)
    mails = grpc_client.list_outbox(username)
    selected_mail = None
    for mail in mails:
        if mail.id == mail_id:
            selected_mail = mail
            break

    if selected_mail is None:
        return HttpResponseRedirect(reverse("mailapp:outbox"))

    return render(
        request,
        "mailapp/mail_detail.html",
        {"mail": selected_mail, "box": "outbox"},
    )


def delete_inbox_view(request, mail_id):
    redirect = _require_login(request)
    if redirect is not None:
        return redirect

    username = _get_logged_user(request)
    if request.method == "POST":
        grpc_client.delete_inbox_mail(mail_id, username)
    return HttpResponseRedirect(reverse("mailapp:inbox"))


def delete_outbox_view(request, mail_id):
    redirect = _require_login(request)
    if redirect is not None:
        return redirect

    username = _get_logged_user(request)
    if request.method == "POST":
        grpc_client.delete_outbox_mail(mail_id, username)
    return HttpResponseRedirect(reverse("mailapp:outbox"))
