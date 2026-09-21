# Backend

## Asistente inteligente MIRA

El módulo `com.comedor.backend.chatbot` recomienda platos a partir de los platos activos,
sus cantidades por porción y el stock actual. El endpoint es `POST /api/v1/chatbot/messages`
y requiere el mismo JWT que el resto de la aplicación.

Para activar las respuestas generativas, configura la clave únicamente en el entorno del
backend:

```powershell
$env:GEMINI_API_KEY="tu-clave-de-Google-AI-Studio"
```

El modelo se puede cambiar con `GEMINI_MODEL`; el valor predeterminado es
`gemini-2.5-flash-lite`. Si la clave falta o el proveedor no está disponible, el endpoint
sigue respondiendo con un cálculo local determinista basado en el inventario y marca la
respuesta con `generatedBy: "LOCAL"`.

Ejemplo de solicitud:

```json
{
  "message": "¿Qué plato podemos preparar hoy?",
  "portions": 80,
  "history": []
}
```
