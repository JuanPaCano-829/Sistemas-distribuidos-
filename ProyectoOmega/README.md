# ProyectoOmega

Estructura base del proyecto `TurboMessage` siguiendo el estilo de los ejemplos vistos en clase:

- `grpc_server/`
  - Servidor gRPC
  - Persistencia simple con `sqlite3`
  - Archivos generados por Protocol Buffers
- `protos/`
  - Archivo `.proto`
- `web/`
  - Proyecto Django
  - App `mailapp`
  - Templates de la interfaz web
- `data/`
  - Base de datos de TurboMessage

## Ejecucion esperada

1. Instalar dependencias:
   - `django`
   - `grpcio`
   - `grpcio-tools`
2. Iniciar el servidor gRPC:
   - `python grpc_server/server.py`
3. Iniciar Django:
   - `python web/manage.py runserver`
