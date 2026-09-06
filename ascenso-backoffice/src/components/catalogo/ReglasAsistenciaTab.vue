<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import EstadoBadge from '@/components/common/EstadoBadge.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import PaginacionTabla from '@/components/common/PaginacionTabla.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { reglaAsistenciaService } from '@/services/catalogo/ReglaAsistenciaService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { ReglaAsistencia, UnidadAsistencia } from '@/types/catalogo'

const PAGE_SIZE = 10

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('CATALOGO_EDITAR'))

const reglas = ref<ReglaAsistencia[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const filtroEstado = ref<'todas' | 'vigente' | 'historicas'>('todas')
const pagina = ref(1)

const reglasOrdenadas = computed(() =>
  [...reglas.value].sort((a, b) => b.vigenteDesde.localeCompare(a.vigenteDesde)),
)

// La regla vigente es la de fecha más reciente que ya inició — mismo criterio
// que el dominio del backend (ReglaAsistencia, ver comentario en el modelo).
const idVigente = computed(() => {
  const hoy = new Date().toISOString().slice(0, 10)
  return reglasOrdenadas.value.find((r) => r.vigenteDesde <= hoy)?.id ?? null
})

const reglasFiltradas = computed(() =>
  reglasOrdenadas.value.filter((r) => {
    if (filtroEstado.value === 'vigente') return r.id === idVigente.value
    if (filtroEstado.value === 'historicas') return r.id !== idVigente.value
    return true
  }),
)

const totalPaginas = computed(() => Math.max(1, Math.ceil(reglasFiltradas.value.length / PAGE_SIZE)))
const reglasPagina = computed(() => {
  const inicio = (pagina.value - 1) * PAGE_SIZE
  return reglasFiltradas.value.slice(inicio, inicio + PAGE_SIZE)
})

watch(filtroEstado, () => {
  pagina.value = 1
})

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    reglas.value = await reglaAsistenciaService.listar()
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar las reglas de asistencia.'
  } finally {
    cargando.value = false
  }
}

cargar()

const formOpen = ref(false)
const reglaEnEdicion = ref<ReglaAsistencia | null>(null)
const unidad = ref<UnidadAsistencia>('PORCENTAJE')
const minimoRequerido = ref(0)
const vigenteDesde = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirCrear() {
  reglaEnEdicion.value = null
  unidad.value = 'PORCENTAJE'
  minimoRequerido.value = 0
  vigenteDesde.value = ''
  formError.value = null
  formOpen.value = true
}

function abrirEditar(r: ReglaAsistencia) {
  reglaEnEdicion.value = r
  unidad.value = r.unidad
  minimoRequerido.value = r.minimoRequerido
  vigenteDesde.value = r.vigenteDesde
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !vigenteDesde.value) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request = {
    unidad: unidad.value,
    minimoRequerido: minimoRequerido.value,
    vigenteDesde: vigenteDesde.value,
  }
  try {
    if (reglaEnEdicion.value) {
      const actualizada = await reglaAsistenciaService.actualizar(reglaEnEdicion.value.id, request)
      const idx = reglas.value.findIndex((r) => r.id === actualizada.id)
      if (idx >= 0) reglas.value[idx] = actualizada
    } else {
      const creada = await reglaAsistenciaService.crear(request)
      reglas.value.push(creada)
    }
    formOpen.value = false
  } catch (error) {
    formError.value =
      error instanceof ApiClientError ? error.message : 'No se pudo guardar la regla de asistencia.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <div class="flex flex-wrap items-center gap-3">
        <p class="text-sm text-mk-text-muted">
          Histórico — la vigente es la de fecha más reciente que ya inició.
        </p>

        <select
          v-model="filtroEstado"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
        >
          <option value="todas">Todas</option>
          <option value="vigente">Vigente</option>
          <option value="historicas">Históricas</option>
        </select>
      </div>

      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nueva regla
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
            <th class="px-4 py-3">Vigente desde</th>
            <th class="px-4 py-3">Unidad</th>
            <th class="px-4 py-3">Mínimo requerido</th>
            <th class="px-4 py-3">Estado</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="reglasPagina.length === 0">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">
              No hay reglas de asistencia para mostrar.
            </td>
          </tr>
          <tr v-for="r in reglasPagina" v-else :key="r.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ r.vigenteDesde }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ r.unidad }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">
              {{ r.minimoRequerido }}{{ r.unidad === 'PORCENTAJE' ? '%' : '' }}
            </td>
            <td class="px-4 py-2.5">
              <EstadoBadge v-if="r.id === idVigente" variant="success" label="Vigente" />
              <EstadoBadge v-else variant="neutral" label="Histórica" />
            </td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(r)">
                  <ActionIcon name="edit" />
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
      :title="reglaEnEdicion ? 'Editar regla de asistencia' : 'Nueva regla de asistencia'"
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
          <label class="mb-1 block text-sm font-medium text-mk-text">Unidad</label>
          <select
            v-model="unidad"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option value="PORCENTAJE">Porcentaje</option>
            <option value="SESIONES">Sesiones</option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Mínimo requerido</label>
          <input
            v-model.number="minimoRequerido"
            type="number"
            min="0"
            step="0.01"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Vigente desde</label>
          <input
            v-model="vigenteDesde"
            type="date"
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
