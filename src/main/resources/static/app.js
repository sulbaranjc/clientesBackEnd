'use strict';

const API = '/clientes';
const CAMPOS = ['nombre', 'apellido', 'email', 'telefono', 'direccion'];

let clientes = null;     // null = todavía no se han cargado
let vista = localStorage.getItem('vista') || 'tabla';
let idEditando = null;

const $ = (id) => document.getElementById(id);

// ===== Tema (Día / Noche) y vista (Tabla / Cuadrícula) =====
function cambiarTema(tema) {
    document.documentElement.dataset.tema = tema;
    localStorage.setItem('tema', tema);
    marcarBoton('tema', tema);
}

function cambiarVista(nuevaVista) {
    vista = nuevaVista;
    localStorage.setItem('vista', nuevaVista);
    marcarBoton('vista', nuevaVista);
    if (clientes !== null) {
        pintar();
    }
}

// Marca como activo el botón elegido
function marcarBoton(tipo, valor) {
    document.querySelectorAll(`button[data-${tipo}]`).forEach((boton) =>
        boton.setAttribute('aria-pressed', boton.dataset[tipo] === valor));
}

// ===== Cargar y mostrar los clientes =====
async function cargarClientes() {
    mostrarEstado('Cargando clientes…');
    try {
        const respuesta = await fetch(`${API}/listado`);
        if (!respuesta.ok) {
            throw new Error('Error ' + respuesta.status);
        }
        clientes = await respuesta.json();
        pintar();
    } catch (error) {
        clientes = null;
        $('contador').textContent = '';
        mostrarEstado('No se pudo cargar la lista de clientes. Comprueba que el servidor está en marcha.', true);
    }
}

function pintar() {
    $('contador').textContent = `(${clientes.length})`;

    if (clientes.length === 0) {
        mostrarEstado('No hay clientes.');
        return;
    }

    $('estado').hidden = true;
    $('vista-tabla').hidden = vista !== 'tabla';
    $('vista-cuadricula').hidden = vista !== 'cuadricula';

    if (vista === 'tabla') {
        $('cuerpo-tabla').replaceChildren(...clientes.map(crearFila));
    } else {
        $('vista-cuadricula').replaceChildren(...clientes.map(crearTarjeta));
    }
}

// Muestra un texto (cargando, sin clientes o error) en lugar del listado
function mostrarEstado(texto, esError = false) {
    $('estado').textContent = texto;
    $('estado').classList.toggle('estado--error', esError);
    $('estado').hidden = false;
    $('vista-tabla').hidden = true;
    $('vista-cuadricula').hidden = true;
}

// Los campos vacíos se muestran como "—"
function texto(valor) {
    return valor ? String(valor) : '—';
}

// Importante: se usa textContent, así el texto del cliente nunca se interpreta como HTML
function crearFila(cliente) {
    const fila = document.createElement('tr');
    const valores = [cliente.idCliente, cliente.nombre, cliente.apellido,
        cliente.email, cliente.telefono, cliente.direccion];

    for (const valor of valores) {
        const celda = document.createElement('td');
        celda.textContent = texto(valor);
        fila.append(celda);
    }

    const acciones = document.createElement('td');
    acciones.className = 'botones';
    acciones.append(...crearBotones(cliente));
    fila.append(acciones);
    return fila;
}

function crearTarjeta(cliente) {
    const tarjeta = document.createElement('div');
    tarjeta.className = 'tarjeta';

    const titulo = document.createElement('h3');
    titulo.textContent = `${cliente.nombre} ${cliente.apellido}`;
    tarjeta.append(titulo);

    const datos = [['ID', cliente.idCliente], ['Email', cliente.email],
        ['Teléfono', cliente.telefono], ['Dirección', cliente.direccion]];

    for (const [etiqueta, valor] of datos) {
        const linea = document.createElement('p');
        const nombre = document.createElement('strong');
        nombre.textContent = etiqueta + ': ';
        linea.append(nombre, texto(valor));
        tarjeta.append(linea);
    }

    const botones = document.createElement('div');
    botones.className = 'botones';
    botones.append(...crearBotones(cliente));
    tarjeta.append(botones);
    return tarjeta;
}

function crearBotones(cliente) {
    const editar = document.createElement('button');
    editar.textContent = 'Editar';
    editar.className = 'btn btn--editar';
    editar.addEventListener('click', () => abrirEdicion(cliente));

    const eliminar = document.createElement('button');
    eliminar.textContent = 'Eliminar';
    eliminar.className = 'btn btn--eliminar';
    eliminar.addEventListener('click', () => eliminarCliente(cliente));

    return [editar, eliminar];
}

function mostrarMensaje(texto, esError = false) {
    $('mensaje').textContent = texto;
    $('mensaje').classList.toggle('mensaje--error', esError);
    $('mensaje').hidden = false;
}

// ===== Editar (PUT /clientes/{id}) =====
function abrirEdicion(cliente) {
    idEditando = cliente.idCliente;
    $('titulo-editar').textContent = `Editar cliente #${cliente.idCliente}`;
    for (const campo of CAMPOS) {
        $(campo).value = cliente[campo] || '';
    }
    $('error-formulario').hidden = true;
    $('modal-editar').showModal();
}

async function guardarCambios(evento) {
    evento.preventDefault();

    // Los campos vacíos se envían como null
    const datos = {};
    for (const campo of CAMPOS) {
        const valor = $(campo).value.trim();
        datos[campo] = valor === '' ? null : valor;
    }

    try {
        const respuesta = await fetch(`${API}/${idEditando}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });

        if (respuesta.ok) {
            $('modal-editar').close();
            mostrarMensaje('Cliente actualizado correctamente.');
            cargarClientes();
        } else if (respuesta.status === 400) {
            // El backend devuelve los errores así: {"nombre": "El nombre es obligatorio"}
            const errores = await respuesta.json();
            $('error-formulario').textContent = Object.values(errores).join('. ');
            $('error-formulario').hidden = false;
        } else if (respuesta.status === 404) {
            $('modal-editar').close();
            mostrarMensaje('Este cliente ya no existe.', true);
            cargarClientes();
        } else {
            throw new Error('Error ' + respuesta.status);
        }
    } catch (error) {
        $('error-formulario').textContent = 'No se pudo guardar el cliente.';
        $('error-formulario').hidden = false;
    }
}

// ===== Eliminar (DELETE /clientes/{id}) =====
async function eliminarCliente(cliente) {
    if (!confirm(`¿Seguro que quieres eliminar a ${cliente.nombre} ${cliente.apellido}?`)) {
        return;
    }

    try {
        const respuesta = await fetch(`${API}/${cliente.idCliente}`, { method: 'DELETE' });
        if (respuesta.status === 204) {
            mostrarMensaje('Cliente eliminado.');
        } else if (respuesta.status === 404) {
            mostrarMensaje('Este cliente ya no existía.', true);
        } else {
            mostrarMensaje('No se pudo eliminar el cliente.', true);
        }
    } catch (error) {
        mostrarMensaje('No se pudo conectar con el servidor.', true);
    }
    cargarClientes();
}

// ===== Eventos =====
document.querySelectorAll('button[data-tema]').forEach((boton) =>
    boton.addEventListener('click', () => cambiarTema(boton.dataset.tema)));

document.querySelectorAll('button[data-vista]').forEach((boton) =>
    boton.addEventListener('click', () => cambiarVista(boton.dataset.vista)));

$('form-editar').addEventListener('submit', guardarCambios);
$('btn-cancelar').addEventListener('click', () => $('modal-editar').close());

// ===== Inicio =====
marcarBoton('tema', document.documentElement.dataset.tema);
marcarBoton('vista', vista);
cargarClientes();
