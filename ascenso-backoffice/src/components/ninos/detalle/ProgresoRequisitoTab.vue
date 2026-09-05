<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma, Grupo, PasoRequerido } from '@/types/catalogo'

interface Registro {
  id: number
  pasoRequeridoId: number
  fechaCompletado: string
}

const props = defineProps<{
  grupos: Grupo[]
  aniosPrograma: AnioPrograma[]
  pasosRequeridos: PasoRequerido[]
  grupoInicialId?: number | null
  listar: () => Promise<Registro[]>
  registrar: (pasoRequeridoId: number, fechaCompletado: string) => Promise<Registro>
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
      error instanceof ApiClientError ? error.message : 'No se pudo cargar el progreso de pasos requeridos.'
  } finally {
    cargando.value = false
  }
}

cargar()

function nombreGrupo(id: number): string {
  return props.grupos.find((g) => g.id === id)?.nombre ?? `Grupo #${id}`
}

function etiquetaAnio(anioProgramaId: number): string {
  const a = props.aniosPrograma.find((x) => x.id === anioProgramaId)
  return a ? `${nombreGrupo(a.grupoId)} · Año ${a.numero}` : `Año #${anioProgramaId}`
}

function etiquetaPaso(id: number): string {
  const p = props.pasosRequeridos.find((x) => x.id === id)
  return p ? `${p.descripcion} (${etiquetaAnio(p.anioProgramaId)})` : `#${id}`
}

const formOpen = ref(false)
const grupoId = ref<number | null>(null)
const anioProgramaId = ref<number | null>(null)
const pasoRequeridoId = ref<number | null>(null)
const fechaCompletado = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

const aniosDelGrupo = computed(() =>
  props.aniosPrograma.filter((a) => a.grupoId === grupoId.value).sort((a, b) => a.numero - b.numero),
)
const pasosDelAnio = computed(() =>
  props.pasosRequeridos.filter((p) => p.anioProgramaId === anioProgramaId.value),
)

watch(grupoId, () => {
  if (!aniosDelGrupo.value.some((a) => a.id === anioProgramaId.value)) {
    anioProgramaId.value = aniosDelGrupo.value[0]?.id ?? null
  }
})

watch(anioProgramaId, () => {
  if (!pasosDelAnio.value.some((p) => p.id === pasoRequeridoId.value)) {
    pasoRequeridoId.value = pasosDelAnio.value[0]?.id ?? null
  }
})

function abrirCrear() {
  grupoId.value = props.grupoInicialId ?? props.grupos[0]?.id ?? null
  anioProgramaId.value = aniosDelGrupo.value[0]?.id ?? null
  pasoRequeridoId.value = pasosDelAnio.value[0]?.id ?? null
  fechaCompletado.value = ''
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !pasoRequeridoId.value || !fechaCompletado.value) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  try {
    const creado = await props.registrar(pasoRequeridoId.value, fechaCompletado.value)
    registros.value.push(creado)
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo registrar el paso.'
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
        Registrar paso completado
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
            <th class="px-4 py-3">Paso requerido</th>
            <th class="px-4 py-3">Fecha completado</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="2" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="registros.length === 0">
            <td colspan="2" class="px-4 py-6 text-center text-mk-text-muted">
              Sin pasos registrados todavía.
            </td>
          </tr>
          <tr v-for="r in registros" v-else :key="r.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ etiquetaPaso(r.pasoRequeridoId) }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ r.fechaCompletado }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <ModalDialog v-model="formOpen" title="Registrar paso completado">
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
          <label class="mb-1 block text-sm font-medium text-mk-text">Año de programa</label>
          <select
            v-model.number="anioProgramaId"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option v-for="a in aniosDelGrupo" :key="a.id" :value="a.id">Año {{ a.numero }}</option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Paso requerido</label>
          <select
            v-model.number="pasoRequeridoId"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option v-for="p in pasosDelAnio" :key="p.id" :value="p.id">{{ p.descripcion }}</option>
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
