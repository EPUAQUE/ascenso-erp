<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { usePermissionsStore } from '@/stores/permissions.store'
import { usuarioService } from '@/services/seguridad/UsuarioService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { CrearUsuarioRequest, EstadoUsuario, Usuario } from '@/types/seguridad'
import ModalDialog from '@/components/common/ModalDialog.vue'
import EstadoBadge from '@/components/common/EstadoBadge.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import PaginacionTabla from '@/components/common/PaginacionTabla.vue'

const PAGE_SIZE = 10

const router = useRouter()
const permissions = usePermissionsStore()
const puedeCrear = computed(() => permissions.can('USUARIOS_CREAR'))

const usuarios = ref<Usuario[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

const busqueda = ref('')
const filtroEstado = ref<'todos' | 'activos' | 'inactivos' | 'bloqueados'>('activos')
const pagina = ref(1)

function estadoBadge(estado: EstadoUsuario): { variant: 'success' | 'neutral' | 'danger'; label: string } {
  if (estado === 'ACTIVO') return { variant: 'success', label: 'Activo' }
  if (estado === 'BLOQUEADO') return { variant: 'danger', label: 'Bloqueado' }
  return { variant: 'neutral', label: 'Inactivo' }
}

const usuariosFiltrados = computed(() => {
  const q = busqueda.value.trim().toLowerCase()
  return usuarios.value.filter((u) => {
    if (filtroEstado.value === 'activos' && u.estado !== 'ACTIVO') return false
    if (filtroEstado.value === 'inactivos' && u.estado !== 'INACTIVO') return false
    if (filtroEstado.value === 'bloqueados' && u.estado !== 'BLOQUEADO') return false
    if (q && !u.username.toLowerCase().includes(q) && !u.nombre.toLowerCase().includes(q)) return false
    return true
  })
})

const totalPaginas = computed(() => Math.max(1, Math.ceil(usuariosFiltrados.value.length / PAGE_SIZE)))
const usuariosPagina = computed(() => {
  const inicio = (pagina.value - 1) * PAGE_SIZE
  return usuariosFiltrados.value.slice(inicio, inicio + PAGE_SIZE)
})

watch([busqueda, filtroEstado], () => {
  pagina.value = 1
})

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    usuarios.value = await usuarioService.listar()
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar la lista de usuarios.'
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)

function verDetalle(u: Usuario) {
  router.push({ name: 'usuario-detalle', params: { id: u.id } })
}

const formOpen = ref(false)
const username = ref('')
const password = ref('')
const nombre = ref('')
const telefono = ref('')
const correo = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirCrear() {
  username.value = ''
  password.value = ''
  nombre.value = ''
  telefono.value = ''
  correo.value = ''
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !username.value.trim() || !password.value || !nombre.value.trim()) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request: CrearUsuarioRequest = {
    username: username.value.trim(),
    password: password.value,
    nombre: nombre.value.trim(),
    telefono: telefono.value.trim() || null,
    correo: correo.value.trim() || null,
  }
  try {
    const creado = await usuarioService.crear(request)
    usuarios.value.unshift(creado)
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo crear el usuario.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="space-y-4 p-6">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <div class="flex flex-wrap items-center gap-3">
        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar por usuario o nombre…"
          class="mk-input w-56 rounded-md border border-mk-border px-3 py-1.5 text-sm"
        />
        <select
          v-model="filtroEstado"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
        >
          <option value="activos">Activos</option>
          <option value="inactivos">Inactivos</option>
          <option value="bloqueados">Bloqueados</option>
          <option value="todos">Todos</option>
        </select>
      </div>

      <button v-if="puedeCrear" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo usuario
      </button>
    </div>

    <p
      v-if="errorMessage"
      class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>

    <div class="mk-card mk-scroll-x overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead>
          <tr
            class="border-b border-mk-border text-xs font-semibold uppercase tracking-wider text-mk-text-muted"
          >
            <th class="px-4 py-3">Usuario</th>
            <th class="px-4 py-3">Nombre</th>
            <th class="px-4 py-3">Correo</th>
            <th class="px-4 py-3">Teléfono</th>
            <th class="px-4 py-3">Estado</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="6" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="usuariosPagina.length === 0">
            <td colspan="6" class="px-4 py-6 text-center text-mk-text-muted">
              No hay usuarios para mostrar.
            </td>
          </tr>
          <tr v-for="u in usuariosPagina" v-else :key="u.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ u.username }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ u.nombre }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ u.correo ?? '—' }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ u.telefono ?? '—' }}</td>
            <td class="px-4 py-2.5">
              <EstadoBadge v-bind="estadoBadge(u.estado)" />
            </td>
            <td class="px-4 py-2.5">
              <div class="mk-row-actions justify-end">
                <button
                  type="button"
                  class="mk-row-btn mk-row-btn-neutral"
                  title="Ver accesos y acciones"
                  @click="verDetalle(u)"
                >
                  <ActionIcon name="eye" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="totalPaginas > 1" class="flex justify-end">
      <PaginacionTabla v-model:pagina="pagina" :total-paginas="totalPaginas" />
    </div>

    <ModalDialog v-model="formOpen" title="Nuevo usuario">
      <form class="space-y-4" @submit.prevent="onSubmit">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Usuario</label>
          <input
            v-model="username"
            type="text"
            maxlength="100"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Contraseña</label>
          <input
            v-model="password"
            type="password"
            maxlength="256"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Nombre completo</label>
          <input
            v-model="nombre"
            type="text"
            maxlength="150"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Teléfono (opcional)</label>
            <input
              v-model="telefono"
              type="text"
              maxlength="20"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Correo (opcional)</label>
            <input
              v-model="correo"
              type="email"
              maxlength="150"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
        </div>

        <div class="flex justify-end gap-2 pt-2">
          <button type="button" class="mk-btn mk-btn-ghost" @click="formOpen = false">Cancelar</button>
          <button type="submit" :disabled="guardando" class="mk-btn mk-btn-primary">
            {{ guardando ? 'Guardando…' : 'Guardar' }}
          </button>
        </div>
      </form>
    </ModalDialog>
  </div>
</template>
