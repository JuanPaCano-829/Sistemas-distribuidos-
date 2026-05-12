from django.urls import include, path


urlpatterns = [
    path("", include("mailapp.urls")),
]
