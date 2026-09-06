<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import PaginacionTabla from '@/components/common/PaginacionTabla.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { anioProgramaService } from '@/services/catalogo/AnioProgramaService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma, Medalla } from '@/types/catalogo'

const PAGE_SIZE = 10

const props = defineProps<{ grupoId: number }>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('CATALOGO_EDITAR'))

const anios = ref<AnioPrograma[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const pagina = ref(1)

const totalPaginas = computed(() => Math.max(1, Math.ceil(anios.value.length / PAGE_SIZE)))
const aniosPagina = computed(() => {
  const inicio = (pagina.value - 1) * PAGE_SIZE
  return anios.value.slice(inicio, inicio + PAGE_SIZE)
})

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  pagina.value = 1
  try {
    anios.value = await anioProgramaService.listar(props.grupoId)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar los años de programa.'
  } finally {
    cargando.value = false
  }
}

watch(() => props.grupoId, cargar)
cargar()

const formOpen = ref(false)
const anioEnEdicion = ref<AnioPrograma | null>(null)
const numero = ref(1)
const medalla = ref<Medalla | ''>('')
const minimoLibros = ref(0)
const minimoDestrezas = ref(0)
const minimoLiderazgo = ref(0)
const esAnioGracia = ref(false)
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirCrear() {
  anioEnEdicion.value = null
  numero.value = 1
  medalla.value = ''
  minimoLibros.value = 0
  minimoDestrezas.value = 0
  minimoLiderazgo.value = 0
  esAnioGracia.value = false
  formError.value = null
  formOpen.value = true
}

function abrirEditar(a: AnioPrograma) {
  anioEnEdicion.value = a
  numero.value = a.numero
  medalla.value = a.medalla ?? ''
  minimoLibros.value = a.minimoLibros
  minimoDestrezas.value = a.minimoDestrezas
  minimoLiderazgo.value = a.minimoLiderazgo
  esAnioGracia.value = a.esAnioGracia
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value) return
  guardando.value = true
  formError.value = null
  const request = {
    grupoId: props.grupoId,
    numero: numero.value,
    medalla: medalla.value || null,
    minimoLibros: minimoLibros.value,
    minimoDestrezas: minimoDestrezas.value,
    minimoLiderazgo: minimoLiderazgo.value,
    esAnioGracia: esAnioGracia.value,
  }
  try {
    if (anioEnEdicion.value) {
      const actualizado = await anioProgramaService.actualizar(anioEnEdicion.value.id, request)
      const idx = anios.value.findIndex((a) => a.id === actualizado.id)
      if (idx >= 0) anios.value[idx] = actualizado
    } else {
      const creado = await anioProgramaService.crear(request)
      anios.value.push(creado)
    }
    formOpen.value = false
  } catch (error) {
    formError.value =
      error instanceof ApiClientError ? error.message : 'No se pudo guardar el año de programa.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex justify-end">
      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo año de programa
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
            <th class="px-4 py-3">Medalla</th>
            <th class="px-4 py-3">Mín. libros</th>
            <th class="px-4 py-3">Mín. destrezas</th>
            <th class="px-4 py-3">Mín. liderazgo</th>
            <th class="px-4 py-3">Año de gracia</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="7" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="anios.length === 0">
            <td colspan="7" class="px-4 py-6 text-center text-mk-text-muted">
              No hay años de programa para mostrar.
            </td>
          </tr>
          <tr v-for="a in aniosPagina" v-else :key="a.id">
            <td class="mk-num px-4 py-2.5 font-medium text-mk-text">{{ a.numero }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ a.medalla ?? '—' }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ a.minimoLibros }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ a.minimoDestrezas }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ a.minimoLiderazgo }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ a.esAnioGracia ? 'Sí' : 'No' }}</td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(a)">
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
      :title="anioEnEdicion ? 'Editar año de programa' : 'Nuevo año de programa'"
    >
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
            <label class="mb-1 block text-sm font-medium text-mk-text">Número</label>
            <input
              v-model.number="numero"
              type="number"
              min="1"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Medalla (opcional)</label>
            <select
              v-model="medalla"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            >
              <option value="">Ninguna</option>
              <option value="BRONCE">BRONCE</option>
              <option value="PLATA">PLATA</option>
              <option value="ORO">ORO</option>
            </select>
          </div>
        </div>

        <div class="grid grid-cols-3 gap-4">
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Mín. libros</label>
            <input
              v-model.number="minimoLibros"
              type="number"
              min="0"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Mín. destrezas</label>
            <input
              v-model.number="minimoDestrezas"
              type="number"
              min="0"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Mín. liderazgo</label>
            <input
              v-model.number="minimoLiderazgo"
              type="number"
              min="0"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
        </div>

        <label class="flex items-center gap-2 text-sm text-mk-text">
          <input v-model="esAnioGracia" type="checkbox" class="rounded border-mk-border" />
          Año de gracia (sin medalla, orientación)
        </label>

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
