<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { usuarioService } from '@/services/seguridad/UsuarioService'
import { rolService } from '@/services/seguridad/RolService'
import { destacamentoService } from '@/services/destacamentos/DestacamentoService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { EstadoUsuario, Rol, Usuario, UsuarioDestacamento } from '@/types/seguridad'
import type { Destacamento } from '@/types/destacamentos'
import ModalDialog from '@/components/common/ModalDialog.vue'
import EstadoBadge from '@/components/common/EstadoBadge.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'

const props = defineProps<{ id: number }>()

const permissions = usePermissionsStore()
const puedeAsignar = computed(() => permissions.can('USUARIOS_ASIGNAR_DESTACAMENTO'))
const puedeRestablecerPassword = computed(() => permissions.can('USUARIOS_RESTABLECER_PASSWORD'))
const puedeRevocarSesiones = computed(() => permissions.can('USUARIOS_REVOCAR_SESIONES'))
const puedeCambiarEstado = computed(() => permissions.can('USUARIOS_CAMBIAR_ESTADO'))

const usuario = ref<Usuario | null>(null)
const asignaciones = ref<UsuarioDestacamento[]>([])
const destacamentos = ref<Destacamento[]>([])
const roles = ref<Rol[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const accionMessage = ref<string | null>(null)

function nombreDestacamento(id: number): string {
  return destacamentos.value.find((d) => d.id === id)?.nombre ?? `Destacamento #${id}`
}

function estadoBadge(estado: EstadoUsuario): { variant: 'success' | 'neutral' | 'danger'; label: string } {
  if (estado === 'ACTIVO') return { variant: 'success', label: 'Activo' }
  if (estado === 'BLOQUEADO') return { variant: 'danger', label: 'Bloqueado' }
  return { variant: 'neutral', label: 'Inactivo' }
}

async function cargarAsignaciones() {
  asignaciones.value = await usuarioService.listarDestacamentos(props.id)
}

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    const [todos, dests, rolesResp] = await Promise.all([
      usuarioService.listar(),
      destacamentoService.listar(),
      rolService.listar(),
    ])
    usuario.value = todos.find((u) => u.id === props.id) ?? null
    destacamentos.value = dests
    roles.value = rolesResp
    await cargarAsignaciones()
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar la información del usuario.'
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)

const formOpen = ref(false)
const destacamentoId = ref<number | null>(null)
const rolId = ref<number | null>(null)
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirAsignar() {
  destacamentoId.value = destacamentos.value[0]?.id ?? null
  rolId.value = roles.value[0]?.id ?? null
  formError.value = null
  formOpen.value = true
}

async function onSubmitAsignar() {
  if (guardando.value || !destacamentoId.value || !rolId.value) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  try {
    await usuarioService.asignarDestacamento(props.id, {
      destacamentoId: destacamentoId.value,
      rolId: rolId.value,
    })
    await cargarAsignaciones()
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo asignar el destacamento.'
  } finally {
    guardando.value = false
  }
}

const passwordTemporal = ref<string | null>(null)

// Confirmación propia (ModalDialog) en vez de window.confirm() nativo — un
// confirm() bloquea el hilo de la página hasta que alguien lo cierra a mano,
// lo cual además de verse fuera de lugar frente al resto de la UI, deja
// completamente colgada cualquier automatización de navegador sobre esta
// pantalla (se detectó probando este mismo flujo).
const confirmOpen = ref(false)
const confirmMessage = ref('')
const confirmAction = ref<(() => Promise<void>) | null>(null)

function pedirConfirmacion(mensaje: string, accion: () => Promise<void>) {
  confirmMessage.value = mensaje
  confirmAction.value = accion
  confirmOpen.value = true
}

async function onConfirmar() {
  const accion = confirmAction.value
  confirmOpen.value = false
  if (accion) await accion()
}

function onRestablecerPassword() {
  pedirConfirmacion(
    '¿Restablecer la contraseña de este usuario? Se generará una contraseña temporal.',
    async () => {
      accionMessage.value = null
      errorMessage.value = null
      try {
        const respuesta = await usuarioService.restablecerPassword(props.id)
        passwordTemporal.value = respuesta.passwordTemporal
      } catch (error) {
        errorMessage.value =
          error instanceof ApiClientError ? error.message : 'No se pudo restablecer la contraseña.'
      }
    },
  )
}

function onRevocarSesiones() {
  pedirConfirmacion(
    '¿Revocar todas las sesiones activas de este usuario? Deberá volver a iniciar sesión.',
    async () => {
      accionMessage.value = null
      errorMessage.value = null
      try {
        await usuarioService.revocarSesiones(props.id)
        accionMessage.value = 'Sesiones revocadas.'
      } catch (error) {
        errorMessage.value =
          error instanceof ApiClientError ? error.message : 'No se pudo revocar las sesiones.'
      }
    },
  )
}

async function onCambiarEstado(accion: 'activar' | 'desactivar' | 'bloquear') {
  errorMessage.value = null
  try {
    const actualizado = await usuarioService[accion](props.id)
    usuario.value = actualizado
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cambiar el estado.'
  }
}
</script>

<template>
  <div class="space-y-4 p-6">
    <RouterLink :to="{ name: 'usuarios' }" class="text-sm text-mk-text-muted hover:text-mk-text">
      ← Volver a usuarios
    </RouterLink>

    <p
      v-if="errorMessage"
      class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>
    <p
      v-if="accionMessage"
      class="rounded-md bg-mk-success/10 px-3 py-2 text-sm font-medium text-mk-success"
      role="status"
    >
      {{ accionMessage }}
    </p>
    <div v-if="cargando" class="text-sm text-mk-text-muted">Cargando…</div>

    <template v-if="usuario">
      <div class="mk-card flex flex-wrap items-center justify-between gap-3 px-4 py-3">
        <div>
          <p class="font-bold text-mk-text">{{ usuario.nombre }}</p>
          <p class="text-sm text-mk-text-muted">
            {{ usuario.username }} · {{ usuario.correo ?? 'sin correo' }} ·
            {{ usuario.telefono ?? 'sin teléfono' }}
          </p>
        </div>
        <div class="flex items-center gap-3">
          <EstadoBadge v-bind="estadoBadge(usuario.estado)" />
          <div v-if="puedeCambiarEstado" class="mk-row-actions">
            <button
              v-if="usuario.estado !== 'ACTIVO'"
              type="button"
              class="mk-row-btn mk-row-btn-success"
              title="Activar"
              @click="onCambiarEstado('activar')"
            >
              <ActionIcon name="power" />
            </button>
            <button
              v-if="usuario.estado === 'ACTIVO'"
              type="button"
              class="mk-row-btn mk-row-btn-neutral"
              title="Desactivar"
              @click="onCambiarEstado('desactivar')"
            >
              <ActionIcon name="power" />
            </button>
            <button
              v-if="usuario.estado !== 'BLOQUEADO'"
              type="button"
              class="mk-row-btn mk-row-btn-danger"
              title="Bloquear"
              @click="onCambiarEstado('bloquear')"
            >
              <ActionIcon name="lock" />
            </button>
          </div>
        </div>
      </div>

      <div class="flex flex-wrap gap-2">
        <button
          v-if="puedeRestablecerPassword"
          type="button"
          class="mk-btn mk-btn-outline"
          @click="onRestablecerPassword"
        >
          <ActionIcon name="key" class="h-4 w-4" />
          Restablecer contraseña
        </button>
        <button
          v-if="puedeRevocarSesiones"
          type="button"
          class="mk-btn mk-btn-outline"
          @click="onRevocarSesiones"
        >
          <ActionIcon name="refresh" class="h-4 w-4" />
          Revocar sesiones
        </button>
      </div>

      <p v-if="passwordTemporal" class="rounded-md bg-mk-pending/10 px-3 py-2 text-sm" role="alert">
        Contraseña temporal generada — comunícala al usuario, no volverá a mostrarse:
        <span class="mk-num font-bold text-mk-text">{{ passwordTemporal }}</span>
      </p>

      <div class="flex items-center justify-between">
        <h2 class="text-sm font-semibold text-mk-text">Accesos por destacamento</h2>
        <button v-if="puedeAsignar" type="button" class="mk-btn mk-btn-primary" @click="abrirAsignar">
          <ActionIcon name="plus" class="h-4 w-4" />
          Asignar destacamento
        </button>
      </div>

      <div class="mk-card mk-scroll-x overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead>
            <tr
              class="border-b border-mk-border text-xs font-semibold uppercase tracking-wider text-mk-text-muted"
            >
              <th class="px-4 py-3">Destacamento</th>
              <th class="px-4 py-3">Rol</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-mk-border">
            <tr v-if="asignaciones.length === 0">
              <td colspan="2" class="px-4 py-6 text-center text-mk-text-muted">
                Sin accesos asignados todavía.
              </td>
            </tr>
            <tr v-for="a in asignaciones" v-else :key="a.id">
              <td class="px-4 py-2.5 font-medium text-mk-text">{{ nombreDestacamento(a.destacamentoId) }}</td>
              <td class="px-4 py-2.5 text-mk-text-muted">{{ a.rolNombre }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
    <p v-else-if="!cargando" class="text-sm text-mk-text-muted">Usuario no encontrado.</p>

    <ModalDialog v-model="formOpen" title="Asignar destacamento">
      <form class="space-y-4" @submit.prevent="onSubmitAsignar">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Destacamento</label>
          <select
            v-model.number="destacamentoId"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option v-for="d in destacamentos" :key="d.id" :value="d.id">{{ d.nombre }}</option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Rol</label>
          <select
            v-model.number="rolId"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option v-for="r in roles" :key="r.id" :value="r.id">{{ r.nombre }}</option>
          </select>
        </div>

        <div class="flex justify-end gap-2 pt-2">
          <button type="button" class="mk-btn mk-btn-ghost" @click="formOpen = false">Cancelar</button>
          <button type="submit" :disabled="guardando" class="mk-btn mk-btn-primary">
            {{ guardando ? 'Guardando…' : 'Guardar' }}
          </button>
        </div>
      </form>
    </ModalDialog>

    <ModalDialog v-model="confirmOpen" title="Confirmar acción" max-width="max-w-sm">
      <p class="text-sm text-mk-text">{{ confirmMessage }}</p>
      <div class="flex justify-end gap-2 pt-4">
        <button type="button" class="mk-btn mk-btn-ghost" @click="confirmOpen = false">Cancelar</button>
        <button type="button" class="mk-btn mk-btn-primary" @click="onConfirmar">Confirmar</button>
      </div>
    </ModalDialog>
  </div>
</template>
