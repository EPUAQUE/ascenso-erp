<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import PaginacionTabla from '@/components/common/PaginacionTabla.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { trimestreService } from '@/services/ninos/TrimestreService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Trimestre } from '@/types/ninos'

const PAGE_SIZE = 10

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('TRIMESTRES_EDITAR'))

const trimestres = ref<Trimestre[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

const busqueda = ref('')
const pagina = ref(1)

const trimestresOrdenados = computed(() =>
  [...trimestres.value].sort((a, b) => b.anioCalendario - a.anioCalendario || b.numero - a.numero),
)

const trimestresFiltrados = computed(() => {
  const q = busqueda.value.trim().toLowerCase()
  if (!q) return trimestresOrdenados.value
  return trimestresOrdenados.value.filter(
    (t) => String(t.anioCalendario).includes(q) || String(t.numero).includes(q),
  )
})

const totalPaginas = computed(() => Math.max(1, Math.ceil(trimestresFiltrados.value.length / PAGE_SIZE)))
const trimestresPagina = computed(() => {
  const inicio = (pagina.value - 1) * PAGE_SIZE
  return trimestresFiltrados.value.slice(inicio, inicio + PAGE_SIZE)
})

watch(busqueda, () => {
  pagina.value = 1
})

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    trimestres.value = await trimestreService.listar()
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cargar los trimestres.'
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)

const formOpen = ref(false)
const trimestreEnEdicion = ref<Trimestre | null>(null)
const anioCalendario = ref(new Date().getFullYear())
const numero = ref(1)
const fechaInicio = ref('')
const fechaFin = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirCrear() {
  trimestreEnEdicion.value = null
  anioCalendario.value = new Date().getFullYear()
  numero.value = 1
  fechaInicio.value = ''
  fechaFin.value = ''
  formError.value = null
  formOpen.value = true
}

function abrirEditar(t: Trimestre) {
  trimestreEnEdicion.value = t
  anioCalendario.value = t.anioCalendario
  numero.value = t.numero
  fechaInicio.value = t.fechaInicio
  fechaFin.value = t.fechaFin
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !fechaInicio.value || !fechaFin.value) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request = {
    anioCalendario: anioCalendario.value,
    numero: numero.value,
    fechaInicio: fechaInicio.value,
    fechaFin: fechaFin.value,
  }
  try {
    if (trimestreEnEdicion.value) {
      const actualizado = await trimestreService.actualizar(trimestreEnEdicion.value.id, request)
      const idx = trimestres.value.findIndex((t) => t.id === actualizado.id)
      if (idx >= 0) trimestres.value[idx] = actualizado
    } else {
      const creado = await trimestreService.crear(request)
      trimestres.value.push(creado)
    }
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo guardar el trimestre.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <div class="flex flex-wrap items-center gap-3">
        <p class="text-sm text-mk-text-muted">Calendario global — compartido por todos los destacamentos.</p>
        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar por año o número…"
          class="mk-input w-48 rounded-md border border-mk-border px-3 py-1.5 text-sm"
        />
      </div>

      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo trimestre
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
            <th class="px-4 py-3">Año</th>
            <th class="px-4 py-3">Número</th>
            <th class="px-4 py-3">Fecha inicio</th>
            <th class="px-4 py-3">Fecha fin</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="trimestresPagina.length === 0">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">
              No hay trimestres para mostrar.
            </td>
          </tr>
          <tr v-for="t in trimestresPagina" v-else :key="t.id">
            <td class="mk-num px-4 py-2.5 font-medium text-mk-text">{{ t.anioCalendario }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ t.numero }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ t.fechaInicio }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ t.fechaFin }}</td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(t)">
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

    <ModalDialog v-model="formOpen" :title="trimestreEnEdicion ? 'Editar trimestre' : 'Nuevo trimestre'">
      <form class="space-y-4" @submit.prevent="onSubmit">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Año calendario</label>
            <input
              v-model.number="anioCalendario"
              type="number"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Número (1-4)</label>
            <input
              v-model.number="numero"
              type="number"
              min="1"
              max="4"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Fecha inicio</label>
            <input
              v-model="fechaInicio"
              type="date"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Fecha fin</label>
            <input
              v-model="fechaFin"
              type="date"
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
