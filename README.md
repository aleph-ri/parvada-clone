# Aleph SDK (Android Version)

# Arquitectura del SDK

Toda función dentro del SDK de Aleph nos regresará un flow, el cual puede emitir 3 estados diferente

`State.Success`

`State.Failure`

`State.Progress`

Esto nos permite saber si la función está en estado de cargando, si terminó correctamente, o si terminó con algún fallo.

Si el flow emite `State.Success`, es posible obtener la data ya que el objeto la contiene

Si el flow emite `State.Failure`, es posible obtener que error sucedió en caso de que queramos hacer algo en particular por cada tipo de error

Si el flow emite `State.Progress`, será posible mostrar un loader o lo que prefiramos para poder decirle al usuario que hay datos que están siendo cargados

Todas las funciones son del tipo suspend por lo que es necesario invocarlas desde un CoroutineScope 

Para poder usar todas la funciones a excepción del login es necesario iniciar sesión

# Inicializar el SDK

Para poder inicializar el SDK es necesario contar con una API Key proporcionada por Aleph

`val alephSDK = AlephSDK(Context, apiKey = ApiKey)`

Con esta línea de código ya tendremos listo nuestro objeto para poder usar las funcionalidades que requiramos.

# Login

Esta función nos permite a través de un identificador poder obtener los datos necesarios para iniciar sesión con un usuario en específico.

Nos regresa un flow del tipo Boolean, con el cual sabremos si el inicio de sesión ha sido exitoso o no.

```
alephSDK.login(email = "ofernandez@alephri.com").collect{
when (it) {
        is State.Success -> {
            Log.d("Success", "Success ${it.data}")
        }
        is State.Failure -> {
            Log.d("Failure", "Error ${it.exception}")
        }
        is State.Progress -> {
            // Show a Loader
        }
    }
}
```

# Router

Esta funcionalidad permite obtener un arreglo de hasta 3 rutas ponderadas por riesgo

Retorna un flow del tipo `RiskRouter`

Recibe como parámetro 2 objetos del tipo `GeoPointInput` (un origen y un destino)

`val origin = GeoPointInput(lat = 19.3216323, lon = -99.1790659)`

`val destiny = GeoPointInput(``lat = 19.3215333, lon =  -99.1890659)`

```
alephSDK.getRoute(
    origin,
    destiny
).collect{
when (it) {
        is State.Success -> {
            Log.d("Success", "Router ${it.data}")
        }
        is State.Failure -> {
            Log.d("Failure", "Error ${it.exception}")
        }
        is State.Progress -> {
            // Show a Loader
        }
    }
}
```

# Notification Feed

Esta función nos regresa las notificaciones de cada usuario, para mayor facilidad nos regresa un arreglo de objetos del tipo `Notification`, cada objeto del tipo `Notification` puede ser del subtipo `Alert`, `Message`, `Tracking`

Esta función está paginada, la estrategia del paginado queda a consideración de cada uno

```jsx
alephSDK.getNotifications(10, offset = 0).collect{
when (it) {
        is State.Success -> {
            Log.d("Success", "Notifications ${it.data}")
        }
        is State.Failure -> {
            Log.d("Failure", "Error ${it.exception}")
        }
        is State.Progress -> {
            // Show a Loader
        }
    }
}
```

# Manejar Push notifications

Por razones técnicas es necesaria una integración entre los sistemas del cliente y de Aleph a nivel del backend, ya que es necesario hacer un relay de las notificaciones hacia el sistema del cliente y que sea este el encargado de lanzarlas a su aplicación.

Del lado del SDK se provee una función que obtiene el detalle de la push notification por id.

Regresando una sealed class llamada `Notification`, y que puede ser del del subtipo `Alert`, `Message`, `Tracking`

```jsx
alephSDK.managePushNotification(id = 10).collect{
when (it) {
        is State.Success -> {
            Log.d("Success", "Notification ${it.data}")
        }
        is State.Failure -> {
            Log.d("Failure", "Error ${it.exception}")
        }
        is State.Progress -> {
            // Show a Loader
        }
    }
}
```

# Tracking

La funcionalidad de tracking está implementada desde que se hace login con un usuario en particular, no es necesario hacer nada más.

## Aceptar un trackeo

Para poder ver a un usuario y su recorrido en la consola de Aleph es necesario que el mismo tenga un trackeo activo, el cual puede ser aceptado a través de la notificación de tipo Tracking

usando la funcion acceptTracking(id = id), propia del SDK de Aleph
