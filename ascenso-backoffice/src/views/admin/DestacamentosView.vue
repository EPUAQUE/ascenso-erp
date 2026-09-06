<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { destacamentoService } from '@/services/destacamentos/DestacamentoService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Destacamento, ModoCorteAnio } from '@/types/destacamentos'
import ModalDialog from '@/components/common/ModalDialog.vue'
import EstadoBadge from '@/components/common/EstadoBadge.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import PaginacionTabla from '@/components/common/PaginacionTabla.vue'

const PAGE_SIZE = 10

const permissions = usePermissionsStore()
const puedeCrear = computed(() => permissions.can('DESTACAMENTOS_CREAR'))
const puedeEditar = computed(() => permissions.can('DESTACAMENTOS_EDITAR'))

const destacamentos = ref<Destacamento[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

const busqueda = ref('')
const filtroEstado = ref<'todos' | 'activos' | 'inactivos'>('activos')
const pagina = ref(1)

const destacamentosFiltrados = computed(() => {
  const q = busqueda.value.trim().toLowerCase()
  return destacamentos.value.filter((d) => {
    if (filtroEstado.value === 'activos' && !d.activo) return false
    if (filtroEstado.value === 'inactivos' && d.activo) return false
    if (
      q &&
      !d.nombre.toLowerCase().includes(q) &&
      !d.numeroUnico.toLowerCase().includes(q) &&
      !d.iglesiaNombre.toLowerCase().includes(q)
    )
      return false
    return true
  })
})

const totalPaginas = computed(() => Math.max(1, Math.ceil(destacamentosFiltrados.value.length / PAGE_SIZE)))
const destacamentosPagina = computed(() => {
  const inicio = (pagina.value - 1) * PAGE_SIZE
  return destacamentosFiltrados.value.slice(inicio, inicio + PAGE_SIZE)
})

watch([busqueda, filtroEstado], () => {
  pagina.value = 1
})

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    destacamentos.value = await destacamentoService.listar()
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar la lista de destacamentos.'
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)

const formOpen = ref(false)
const destacamentoEnEdicion = ref<Destacamento | null>(null)
const numeroUnico = ref('')
const nombre = ref('')
const iglesiaNombre = ref('')
const direccion = ref('')
const modoCorteAnio = ref<ModoCorteAnio>('CALENDARIO')
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirCrear() {
  destacamentoEnEdicion.value = null
  numeroUnico.value = ''
  nombre.value = ''
  iglesiaNombre.value = ''
  direccion.value = ''
  modoCorteAnio.value = 'CALENDARIO'
  formError.value = null
  formOpen.value = true
}

function abrirEditar(d: Destacamento) {
  destacamentoEnEdicion.value = d
  numeroUnico.value = d.numeroUnico
  nombre.value = d.nombre
  iglesiaNombre.value = d.iglesiaNombre
  direccion.value = d.direccion
  modoCorteAnio.value = d.modoCorteAnio
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !nombre.value.trim() || !iglesiaNombre.value.trim()) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  if (!destacamentoEnEdicion.value && !numeroUnico.value.trim()) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  try {
    if (destacamentoEnEdicion.value) {
      const actualizado = await destacamentoService.actualizar(destacamentoEnEdicion.value.id, {
        nombre: nombre.value.trim(),
        iglesiaNombre: iglesiaNombre.value.trim(),
        direccion: direccion.value.trim() || null,
        modoCorteAnio: modoCorteAnio.value,
      })
      const idx = destacamentos.value.findIndex((d) => d.id === actualizado.id)
      if (idx >= 0) destacamentos.value[idx] = actualizado
    } else {
      const creado = await destacamentoService.crear({
        numeroUnico: numeroUnico.value.trim(),
        nombre: nombre.value.trim(),
        iglesiaNombre: iglesiaNombre.value.trim(),
        direccion: direccion.value.trim() || null,
        modoCorteAnio: modoCorteAnio.value,
      })
      destacamentos.value.unshift(creado)
    }
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo guardar el destacamento.'
  } finally {
    guardando.value = false
  }
}

async function onToggleActivo(d: Destacamento) {
  errorMessage.value = null
  try {
    if (d.activo) {
      await destacamentoService.desactivar(d.id)
    } else {
      await destacamentoService.activar(d.id)
    }
    d.activo = !d.activo
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cambiar el estado.'
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
          placeholder="Buscar por nombre, número o iglesia…"
          class="mk-input w-64 rounded-md border border-mk-border px-3 py-1.5 text-sm"
        />
        <select
          v-model="filtroEstado"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
        >
          <option value="activos">Activos</option>
          <option value="inactivos">Inactivos</option>
          <option value="todos">Todos</option>
        </select>
      </div>

      <button v-if="puedeCrear" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo destacamento
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
            <th class="px-4 py-3">Número</th>
            <th class="px-4 py-3">Nombre</th>
            <th class="px-4 py-3">Iglesia</th>
            <th class="px-4 py-3">Dirección</th>
            <th class="px-4 py-3">Estado</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="6" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="destacamentosPagina.length === 0">
            <td colspan="6" class="px-4 py-6 text-center text-mk-text-muted">
              No hay destacamentos para mostrar.
            </td>
          </tr>
          <tr v-for="d in destacamentosPagina" v-else :key="d.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ d.numeroUnico }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ d.nombre }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ d.iglesiaNombre }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ d.direccion || '—' }}</td>
            <td class="px-4 py-2.5">
              <EstadoBadge
                :variant="d.activo ? 'success' : 'neutral'"
                :label="d.activo ? 'Activo' : 'Inactivo'"
              />
            </td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(d)">
                  <ActionIcon name="edit" />
                </button>
                <button
                  type="button"
                  class="mk-row-btn"
                  :class="d.activo ? 'mk-row-btn-danger' : 'mk-row-btn-success'"
                  :title="d.activo ? 'Desactivar' : 'Activar'"
                  @click="onToggleActivo(d)"
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
      :title="destacamentoEnEdicion ? 'Editar destacamento' : 'Nuevo destacamento'"
    >
      <form class="space-y-4" @submit.prevent="onSubmit">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div v-if="!destacamentoEnEdicion">
          <label class="mb-1 block text-sm font-medium text-mk-text">Número único</label>
          <input
            v-model="numeroUnico"
            type="text"
            maxlength="20"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Nombre</label>
          <input
            v-model="nombre"
            type="text"
            maxlength="150"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Iglesia</label>
          <input
            v-model="iglesiaNombre"
            type="text"
            maxlength="150"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Dirección (opcional)</label>
          <input
            v-model="direccion"
            type="text"
            maxlength="255"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Modo de corte de año</label>
          <select
            v-model="modoCorteAnio"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option value="CALENDARIO">Calendario</option>
            <option value="ANIVERSARIO_INGRESO">Aniversario de ingreso</option>
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
  </div>
</template>
