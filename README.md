# Sabanales - Sistema de Gestión de Billar

Sistema de gestión para salas de billar con control de jugadores, cuentas individuales, abonos parciales e historial de partidas. Desarrollado en Java con arquitectura hexagonal, patrones Observer y Decorator.

# Tabla de Contenidos

-Características

-Tecnologías

-Arquitectura

# Características

.Gestión de Mesas

.Crear y eliminar mesas

.Estados: Libre, Ocupada, Mantenimiento, Limpieza

.Indicador visual de estado (colores)

.Tooltips con información detallada

# Gestión de Jugadores
.Hasta 5 jugadores por mesa

.Registro individual de cada jugador

.Cuentas independientes por jugador

.Tiempo de juego acumulado

.Pagos y abonos parciales

.Cálculo automático de deuda

.Retiro individual o cierre de mesa

#Sistema de Cobro

.Tarifa base por hora

.Recargos por horario punta (18:00 - 22:00)

.Recargos por fin de semana

.Abonos parciales durante la partida

.Visualización de deuda actual

.Historial de pagos por jugador

# Persistencia

.Guardado automático en archivo plano (serialización)

.Recuperación del estado al iniciar

.No requiere base de datos externa

# Interfaz de Usuario

.Interfaz gráfica con Java Swing

.Actualización en tiempo real (Observer)

.Visualización clara de mesas y jugadores

.Diálogos modales para acciones específicas

.Historial completo de partidas
