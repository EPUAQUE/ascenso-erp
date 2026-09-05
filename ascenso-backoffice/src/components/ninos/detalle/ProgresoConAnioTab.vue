<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma, Grupo } from '@/types/catalogo'

interface CatalogoItem {
  id: number
  grupoId: number
  label: string
}

interface Registro {
  id: number
  catalogoItemId: number
  anioProgramaObjetivoId: number
  fechaCompletado: string
}

const props = defineProps<{
  entidadLabel: string
  grupos: Grupo[]
  aniosPrograma: AnioPrograma[]
  catalogoItems: CatalogoItem[]
  /** Grupo actual del niño — se preselecciona al abrir el formulario de creación. */
  grupoInicialId?: number | null
  listar: () => Promise<Registro[]>
  registrar: (
    catalogoItemId: number,
    anioProgramaObjetivoId: number,
    fechaCompletado: string,
  ) => Promise<Registro>
}>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('NINOS_EDITAR'))

const registros = ref<Registro[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    registros.value = await props.listar()
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError
        ? error.message
        : `No se pudo cargar el progreso de ${props.entidadLabel.toLowerCase()}s.`
  } finally {
    cargando.value = false
  }
}

cargar()

function nombreGrupo(id: number): string {
  return props.grupos.find((g) => g.id === id)?.nombre ?? `Grupo #${id}`
}

function etiquetaCatalogo(id: number): string {
  return props.catalogoItems.find((c) => c.id === id)?.label ?? `#${id}`
}

function etiquetaAnio(id: number): string {
  const a = props.aniosPrograma.find((x) => x.id === id)
  return a ? `${nombreGrupo(a.grupoId)} · Año ${a.numero}` : `Año #${id}`
}

const formOpen = ref(false)
const grupoId = ref<number | null>(null)
const catalogoItemId = ref<number | null>(null)
const anioProgramaObjetivoId = ref<number | null>(null)
const fechaCompletado = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

const itemsDelGrupo = computed(() => props.catalogoItems.filter((c) => c.grupoId === grupoId.value))
const aniosDelGrupo = computed(() =>
  props.aniosPrograma.filter((a) => a.grupoId === grupoId.value).sort((a, b) => a.numero - b.numero),
)

watch(grupoId, () => {
  if (!itemsDelGrupo.value.some((c) => c.id === catalogoItemId.value)) {
    catalogoItemId.value = itemsDelGrupo.value[0]?.id ?? null
  }
  if (!aniosDelGrupo.value.some((a) => a.id === anioProgramaObjetivoId.value)) {
    anioProgramaObjetivoId.value = aniosDelGrupo.value[0]?.id ?? null
  }
})

function abrirCrear() {
  grupoId.value = props.grupoInicialId ?? props.grupos[0]?.id ?? null
  catalogoItemId.value = itemsDelGrupo.value[0]?.id ?? null
  anioProgramaObjetivoId.value = aniosDelGrupo.value[0]?.id ?? null
  fechaCompletado.value = ''
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !catalogoItemId.value || !anioProgramaObjetivoId.value || !fechaCompletado.value) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  try {
    const creado = await props.registrar(
      catalogoItemId.value,
      anioProgramaObjetivoId.value,
      fechaCompletado.value,
    )
    registros.value.push(creado)
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo registrar el progreso.'
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
        Registrar {{ entidadLabel.toLowerCase() }} completado
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
            <th class="px-4 py-3">{{ entidadLabel }}</th>
            <th class="px-4 py-3">Año de programa objetivo</th>
            <th class="px-4 py-3">Fecha completado</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="3" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="registros.length === 0">
            <td colspan="3" class="px-4 py-6 text-center text-mk-text-muted">
              Sin {{ entidadLabel.toLowerCase() }}s registrados todavía.
            </td>
          </tr>
          <tr v-for="r in registros" v-else :key="r.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ etiquetaCatalogo(r.catalogoItemId) }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ etiquetaAnio(r.anioProgramaObjetivoId) }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ r.fechaCompletado }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <ModalDialog v-model="formOpen" :title="`Registrar ${entidadLabel.toLowerCase()} completado`">
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
          <label class="mb-1 block text-sm font-medium text-mk-text">{{ entidadLabel }}</label>
          <select
            v-model.number="catalogoItemId"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option v-for="c in itemsDelGrupo" :key="c.id" :value="c.id">{{ c.label }}</option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Año de programa objetivo</label>
          <select
            v-model.number="anioProgramaObjetivoId"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option v-for="a in aniosDelGrupo" :key="a.id" :value="a.id">Año {{ a.numero }}</option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Fecha completado</label>
          <input
            v-model="fechaCompletado"
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
