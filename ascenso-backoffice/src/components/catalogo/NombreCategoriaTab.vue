<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import EstadoBadge from '@/components/common/EstadoBadge.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Grupo } from '@/types/catalogo'

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
  grupos: Grupo[]
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
const filtroGrupoId = ref<number | null>(null)

const formOpen = ref(false)
const itemEnEdicion = ref<Item | null>(null)
const grupoId = ref<number | null>(null)
const nombre = ref('')
const categoria = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

function nombreGrupo(id: number): string {
  return props.grupos.find((g) => g.id === id)?.nombre ?? `Grupo #${id}`
}

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    items.value = await props.listar(filtroGrupoId.value ?? undefined)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError
        ? error.message
        : `No se pudo cargar la lista de ${props.entidadLabel.toLowerCase()}s.`
  } finally {
    cargando.value = false
  }
}

watch(filtroGrupoId, cargar)
cargar()

function abrirCrear() {
  itemEnEdicion.value = null
  grupoId.value = props.grupos[0]?.id ?? null
  nombre.value = ''
  categoria.value = ''
  formError.value = null
  formOpen.value = true
}

function abrirEditar(item: Item) {
  itemEnEdicion.value = item
  grupoId.value = item.grupoId
  nombre.value = item.nombre
  categoria.value = item.categoria ?? ''
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !grupoId.value || !nombre.value.trim()) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request: Request = {
    grupoId: grupoId.value,
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
      <label class="flex items-center gap-2 text-sm">
        <span class="text-mk-text-muted">Grupo</span>
        <select
          v-model.number="filtroGrupoId"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
        >
          <option :value="null">Todos</option>
          <option v-for="g in grupos" :key="g.id" :value="g.id">{{ g.nombre }}</option>
        </select>
      </label>

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
            <th class="px-4 py-3">Grupo</th>
            <th class="px-4 py-3">Estado</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="items.length === 0">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">
              No hay {{ entidadLabel.toLowerCase() }}s para mostrar.
            </td>
          </tr>
          <tr v-for="item in items" v-else :key="item.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ item.nombre }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ item.categoria ?? '—' }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ nombreGrupo(item.grupoId) }}</td>
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
          <label class="mb-1 block text-sm font-medium text-mk-text">Grupo</label>
          <select
            v-model.number="grupoId"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option v-for="g in grupos" :key="g.id" :value="g.id">{{ g.nombre }}</option>
          </select>
        </div>

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
