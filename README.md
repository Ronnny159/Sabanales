# 🎱 SABANALES - Sistema de Billar

## ¿Qué hace este sistema?

Imagina que tienes una **sala de billar** y necesitas llevar el control de quién juega, cuánto tiempo lleva y cuánto debe pagar. Este sistema te ayuda a hacer todo eso de manera sencilla.

---

## 📝 En palabras simples

### 1. **Gestiona mesas y jugadores**
- Puedes crear mesas (Mesa 1, Mesa 2, etc.)
- En cada mesa pueden jugar hasta **5 personas** al mismo tiempo
- Cada jugador tiene su **propia cuenta** y su **propio tiempo** de juego

### 2. **Lleva el control de cuentas**
- Cada jugador tiene un **contador de tiempo** que va sumando minutos
- Pueden ir **pagando por adelantado** (abonos) mientras juegan
- El sistema siempre muestra cuánto deben en ese momento

### 3. **Cobra de forma inteligente**
- Tiene un **precio por hora** que tú defines
- Aplica **recargos** si juegan en horario de "hora punta" (6pm a 10pm)
- También cobra más los **fines de semana**

### 4. **Guarda todo automáticamente**
- No necesitas una base de datos compleja
- Todo se guarda en un archivo
- Cuando abres el programa, todo está como lo dejaste

### 5. **Tiene historial**
- Guarda el registro de todas las personas que han jugado
- Puedes ver cuánto tiempo estuvo cada uno y cuánto pagó
- Útil para saber cuánto has recaudado

---

## 🎯 ¿Para quién es?

- **Dueños de billares** que quieren automatizar el control
- **Administradores** que necesitan saber quién debe pagar
- **Empleados** que atienden las mesas y cobran

---

## ✅ ¿Qué puede hacer un usuario?

| Acción | ¿Qué hace? |
| :--- | :--- |
| **Agregar jugadores** | Pones los nombres de quienes van a jugar en una mesa |
| **Ver cuenta** | Ves cuánto tiempo lleva, cuánto ha pagado y cuánto debe |
| **Abonar** | El jugador paga una parte mientras sigue jugando |
| **Retirar jugador** | Uno se va, se calcula lo que debe y queda en el historial |
| **Cerrar mesa** | Todos los jugadores se van, se cobra y la mesa queda libre |
| **Ver historial** | Ves quiénes han jugado antes en esa mesa |

---

## 🎨 ¿Cómo se ve?

- **Pantalla principal**: Muestra todas las mesas con colores:
  - 🟢 **Verde** = Libre (puedes jugar)
  - 🔴 **Rojo** = Ocupada (hay gente jugando)
  - ⚪ **Gris** = En mantenimiento (no disponible)

- A la izquierda: los controles para agregar jugadores, ver cuentas y cobrar
- Al centro: las mesas con los jugadores que están en cada una

---

## 💡 Ejemplo de uso

1. Llegan **3 amigos**: Juan, Pedro y Ana
2. El empleado los pone en la **Mesa 1**
3. Cada uno tiene su cuenta:
   - Juan lleva 30 minutos y ha pagado $5
   - Pedro lleva 30 minutos y ha pagado $3
   - Ana lleva 30 minutos y no ha pagado nada
4. Ana decide **abonar $10** para no deber tanto
5. Juan se va después de 1 hora → El sistema calcula que debe $2.50
6. Cuando todos se van, se **cierra la mesa** y queda libre

---

## 🚀 ¿Por qué es útil?

- **Ahorra tiempo**: No necesitas llevar cuentas en papel
- **Evita errores**: El sistema calcula todo automáticamente
- **Es justo**: Cada jugador paga solo por su tiempo
- **Organizado**: Tienes historial de todo lo que pasa

---

## 🏆 En resumen

**Sabanales** es un **sistema amigable** para administrar una sala de billar, donde puedes:
- ✅ Poner varios jugadores por mesa
- ✅ Cada uno con su propia cuenta
- ✅ Cobrar de forma justa y automática
- ✅ Tener un historial completo
- ✅ Todo sin complicaciones técnicas

**¡Perfecto para cualquier negocio de billar!** 🎱
