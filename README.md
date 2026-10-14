# Pan, Puntos y Premios

Aplicación de escritorio Java Swing con persistencia Oracle JDBC.

## Preparación de Oracle

1. Crea o selecciona el esquema `PANPUNTOS` en Oracle XE/PDB.
2. En SQL Developer, ejecuta `database/01_esquema.sql` con **Run Script (F5)**.
3. Ejecuta, en este orden, `database/migraciones/001_clientes_y_premios.sql` y `database/migraciones/002_movimientos_inventario.sql`.
4. Opcionalmente ejecuta `database/02_datos_prueba.sql` para cargar productos, un menú, premios y un cliente de demostración. Los datos se identifican por códigos `DEMO-*`.
5. Revisa la URL, usuario y clave de Oracle en `src/util/Conexion.java`.

Si el esquema ya existe, no vuelvas a ejecutar `01_esquema.sql`: ejecuta solo las migraciones y, si hacen falta registros de demostración, el script de datos.

## Ejecución

Abre el proyecto Ant en Apache NetBeans y ejecútalo. La clase inicial es `view.FrmInicio`. Para compilar desde Ant, usa el objetivo `jar`.

## Flujo principal

- **Nueva venta:** busca un cliente, agrega productos o menús desde los catálogos, revisa total y puntos previstos y continúa al cobro.
- **Cobro:** selecciona pedidos pendientes que sí tienen artículos. Efectivo calcula el cambio; tarjeta simula aprobación o rechazo. El inventario y los puntos se actualizan dentro de la misma transacción que aprueba el pago.
- **Pedidos:** un pedido pendiente se puede anular; solo uno pagado puede marcarse como entregado.
- **Menús:** cada receta debe incluir un sándwich, una bebida y un acompañamiento. El menú tiene su propio precio y descuenta sus componentes al venderse.
- **Canjes:** los premios corresponden a productos o menús reales; el saldo, las existencias y el historial se actualizan juntos.
- **Reportes:** incluye ventas por método, productos vendidos, existencia baja, puntos por cliente, canjes, venta de menús y movimientos de inventario.

El formulario de clientes no solicita NIT. La columna heredada se conserva como opcional para no borrar datos existentes.

## Diagramas

- Modelo de clases: `docs/diagrama_clases.md`
- Modelo entidad-relación: `docs/diagrama_er.md`
