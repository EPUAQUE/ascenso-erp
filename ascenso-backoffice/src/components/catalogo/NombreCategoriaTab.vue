<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import EstadoBadge from '@/components/common/EstadoBadge.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import PaginacionTabla from '@/components/common/PaginacionTabla.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { ApiClientError } from '@/services/http/ApiClient'

const PAGE_SIZE = 10

interface Item {
  id: number
  grupoId: number
  nombre: string
  categoria: string | null
  activo: boolean
}

interface Request {
  grupoId: number
  nombre: string
  categoria: string | null
}

const props = defineProps<{
  entidadLabel: string
  campoCategoriaLabel: string
  grupoId: number
  listar: (grupoId?: number) => Promise<Item[]>
  crear: (request: Request) => Promise<Item>
  actualizar: (id: number, request: Request) => Promise<Item>
  activar: (id: number) => Promise<void>
  desactivar: (id: number) => Promise<void>
}>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('CATALOGO_EDITAR'))

const items = ref<Item[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const filtroEstado = ref<'todos' | 'activos' | 'inactivos'>('activos')
const pagina = ref(1)

const itemsFiltrados = computed(() =>
  items.value.filter((item) => {
    if (filtroEstado.value === 'activos' && !item.activo) return false
    if (filtroEstado.value === 'inactivos' && item.activo) return false
    return true
  }),
)

const totalPaginas = computed(() => Math.max(1, Math.ceil(itemsFiltrados.value.length / PAGE_SIZE)))
const itemsPagina = computed(() => {
  const inicio = (pagina.value - 1) * PAGE_SIZE
  return itemsFiltrados.value.slice(inicio, inicio + PAGE_SIZE)
})

watch(filtroEstado, () => {
  pagina.value = 1
})

const formOpen = ref(false)
const itemEnEdicion = ref<Item | null>(null)
const nombre = ref('')
const categoria = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  pagina.value = 1
  try {
    items.value = await props.listar(props.grupoId)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError
        ? error.message
        : `No se pudo cargar la lista de ${props.entidadLabel.toLowerCase()}s.`
  } finally {
    cargando.value = false
  }
}

watch(() => props.grupoId, cargar)
cargar()

function abrirCrear() {
  itemEnEdicion.value = null
  nombre.value = ''
  categoria.value = ''
  formError.value = null
  formOpen.value = true
}

function abrirEditar(item: Item) {
  itemEnEdicion.value = item
  nombre.value = item.nombre
  categoria.value = item.categoria ?? ''
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !nombre.value.trim()) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request: Request = {
    grupoId: props.grupoId,
    nombre: nombre.value.trim(),
    categoria: categoria.value.trim() || null,
  }
  try {
    if (itemEnEdicion.value) {
      const actualizado = await props.actualizar(itemEnEdicion.value.id, request)
      const idx = items.value.findIndex((i) => i.id === actualizado.id)
      if (idx >= 0) items.value[idx] = actualizado
    } else {
      const creado = await props.crear(request)
      items.value.unshift(creado)
    }
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo guardar.'
  } finally {
    guardando.value = false
  }
}

async function onToggleActivo(item: Item) {
  errorMessage.value = null
  try {
    if (item.activo) {
      await props.desactivar(item.id)
    } else {
      await props.activar(item.id)
    }
    item.activo = !item.activo
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cambiar el estado.'
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <select v-model="filtroEstado" class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm">
        <option value="activos">Activos</option>
        <option value="inactivos">Inactivos</option>
        <option value="todos">Todos</option>
      </select>

      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo {{ entidadLabel.toLowerCase() }}
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
            <th class="px-4 py-3">Nombre</th>
            <th class="px-4 py-3">{{ campoCategoriaLabel }}</th>
            <th class="px-4 py-3">Estado</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="4" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="itemsPagina.length === 0">
            <td colspan="4" class="px-4 py-6 text-center text-mk-text-muted">
              No hay {{ entidadLabel.toLowerCase() }}s para mostrar.
            </td>
          </tr>
          <tr v-for="item in itemsPagina" v-else :key="item.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ item.nombre }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ item.categoria ?? '—' }}</td>
            <td class="px-4 py-2.5">
              <EstadoBadge
                :variant="item.activo ? 'success' : 'neutral'"
                :label="item.activo ? 'Activo' : 'Inactivo'"
              />
            </td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(item)">
                  <ActionIcon name="edit" />
                </button>
                <button
                  type="button"
                  class="mk-row-btn"
                  :class="item.activo ? 'mk-row-btn-danger' : 'mk-row-btn-success'"
                  :title="item.activo ? 'Desactivar' : 'Activar'"
                  @click="onToggleActivo(item)"
                >
                  <ActionIcon name="power" />
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

    <ModalDialog
      v-model="formOpen"
      :title="itemEnEdicion ? `Editar ${entidadLabel.toLowerCase()}` : `Nuevo ${entidadLabel.toLowerCase()}`"
    >
      <form class="space-y-4" @submit.prevent="onSubmit">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Nombre</label>
          <input
            v-model="nombre"
            type="text"
            maxlength="100"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text"
            >{{ campoCategoriaLabel }} (opcional)</label
          >
          <input
            v-model="categoria"
            type="text"
            maxlength="50"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
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
