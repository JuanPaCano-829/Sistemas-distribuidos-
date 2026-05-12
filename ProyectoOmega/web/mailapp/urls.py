from django.urls import path

from . import views


app_name = "mailapp"

urlpatterns = [
    path("", views.login_view, name="login"),
    path("register/", views.register_view, name="register"),
    path("logout/", views.logout_view, name="logout"),
    path("inbox/", views.inbox_view, name="inbox"),
    path("outbox/", views.outbox_view, name="outbox"),
    path("compose/", views.compose_view, name="compose"),
    path("inbox/<int:mail_id>/", views.inbox_detail_view, name="inbox_detail"),
    path("outbox/<int:mail_id>/", views.outbox_detail_view, name="outbox_detail"),
    path("inbox/<int:mail_id>/delete/", views.delete_inbox_view, name="delete_inbox"),
    path("outbox/<int:mail_id>/delete/", views.delete_outbox_view, name="delete_outbox"),
]
