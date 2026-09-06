<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import PaginacionTabla from '@/components/common/PaginacionTabla.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { pasoRequeridoService } from '@/services/catalogo/PasoRequeridoService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma, PasoRequerido } from '@/types/catalogo'

const PAGE_SIZE = 10

const props = defineProps<{ aniosPrograma: AnioPrograma[] }>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('CATALOGO_EDITAR'))

const pasos = ref<PasoRequerido[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const filtroAnioProgramaId = ref<number | null>(null)
const pagina = ref(1)

const aniosOrdenados = computed(() => [...props.aniosPrograma].sort((a, b) => a.numero - b.numero))

const totalPaginas = computed(() => Math.max(1, Math.ceil(pasos.value.length / PAGE_SIZE)))
const pasosPagina = computed(() => {
  const inicio = (pagina.value - 1) * PAGE_SIZE
  return pasos.value.slice(inicio, inicio + PAGE_SIZE)
})

function etiquetaAnio(id: number): string {
  const a = props.aniosPrograma.find((x) => x.id === id)
  return a ? `Año ${a.numero}` : `Año #${id}`
}

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  pagina.value = 1
  try {
    const idsDelGrupo = new Set(props.aniosPrograma.map((a) => a.id))
    const todos = await pasoRequeridoService.listar(filtroAnioProgramaId.value ?? undefined)
    // El backend, sin filtro de año, devuelve TODOS los pasos requeridos del
    // sistema (no solo los del grupo actual) — se filtra client-side a los
    // años de este grupo, igual que se hace con el resto de los sub-catálogos.
    pasos.value = todos.filter((p) => idsDelGrupo.has(p.anioProgramaId))
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar los pasos requeridos.'
  } finally {
    cargando.value = false
  }
}

watch(filtroAnioProgramaId, cargar)
watch(() => props.aniosPrograma, cargar)
cargar()

const formOpen = ref(false)
const pasoEnEdicion = ref<PasoRequerido | null>(null)
const anioProgramaId = ref<number | null>(null)
const descripcion = ref('')
const orden = ref(1)
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirCrear() {
  pasoEnEdicion.value = null
  anioProgramaId.value = filtroAnioProgramaId.value ?? aniosOrdenados.value[0]?.id ?? null
  descripcion.value = ''
  orden.value = 1
  formError.value = null
  formOpen.value = true
}

function abrirEditar(p: PasoRequerido) {
  pasoEnEdicion.value = p
  anioProgramaId.value = p.anioProgramaId
  descripcion.value = p.descripcion
  orden.value = p.orden
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !anioProgramaId.value || !descripcion.value.trim()) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request = {
    anioProgramaId: anioProgramaId.value,
    descripcion: descripcion.value.trim(),
    orden: orden.value,
  }
  try {
    if (pasoEnEdicion.value) {
      const actualizado = await pasoRequeridoService.actualizar(pasoEnEdicion.value.id, request)
      const idx = pasos.value.findIndex((p) => p.id === actualizado.id)
      if (idx >= 0) pasos.value[idx] = actualizado
    } else {
      const creado = await pasoRequeridoService.crear(request)
      pasos.value.push(creado)
    }
    formOpen.value = false
  } catch (error) {
    formError.value =
      error instanceof ApiClientError ? error.message : 'No se pudo guardar el paso requerido.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <label class="flex items-center gap-2 text-sm">
        <span class="text-mk-text-muted">Año</span>
        <select
          v-model.number="filtroAnioProgramaId"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
        >
          <option :value="null">Todos</option>
          <option v-for="a in aniosOrdenados" :key="a.id" :value="a.id">Año {{ a.numero }}</option>
        </select>
      </label>

      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo paso requerido
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
            <th class="px-4 py-3">Descripción</th>
            <th class="px-4 py-3">Orden</th>
            <th class="px-4 py-3">Año de programa</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="4" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="pasosPagina.length === 0">
            <td colspan="4" class="px-4 py-6 text-center text-mk-text-muted">
              No hay pasos requeridos para mostrar.
            </td>
          </tr>
          <tr v-for="p in pasosPagina" v-else :key="p.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ p.descripcion }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ p.orden }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ etiquetaAnio(p.anioProgramaId) }}</td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(p)">
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

    <ModalDialog v-model="formOpen" :title="pasoEnEdicion ? 'Editar paso requerido' : 'Nuevo paso requerido'">
      <form class="space-y-4" @submit.prevent="onSubmit">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Año de programa</label>
          <select
            v-model.number="anioProgramaId"
            :disabled="pasoEnEdicion !== null"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm disabled:opacity-60"
          >
            <option v-for="a in aniosOrdenados" :key="a.id" :value="a.id">Año {{ a.numero }}</option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Descripción</label>
          <input
            v-model="descripcion"
            type="text"
            maxlength="255"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Orden</label>
          <input
            v-model.number="orden"
            type="number"
            min="1"
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
