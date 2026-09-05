<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma, Grupo } from '@/types/catalogo'
import type { MedallaOtorgada } from '@/types/ninos'

const props = defineProps<{
  grupos: Grupo[]
  aniosPrograma: AnioPrograma[]
  grupoInicialId?: number | null
  listar: () => Promise<MedallaOtorgada[]>
  otorgar: (anioProgramaId: number, fechaOtorgada: string) => Promise<MedallaOtorgada>
}>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('NINOS_EDITAR'))

const medallas = ref<MedallaOtorgada[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    medallas.value = await props.listar()
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cargar las medallas.'
  } finally {
    cargando.value = false
  }
}

cargar()

function nombreGrupo(id: number): string {
  return props.grupos.find((g) => g.id === id)?.nombre ?? `Grupo #${id}`
}

function etiquetaAnio(id: number): string {
  const a = props.aniosPrograma.find((x) => x.id === id)
  return a ? `${nombreGrupo(a.grupoId)} · Año ${a.numero}${a.medalla ? ` · ${a.medalla}` : ''}` : `Año #${id}`
}

const formOpen = ref(false)
const grupoId = ref<number | null>(null)
const anioProgramaId = ref<number | null>(null)
const fechaOtorgada = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

const aniosDelGrupo = computed(() =>
  props.aniosPrograma.filter((a) => a.grupoId === grupoId.value).sort((a, b) => a.numero - b.numero),
)

watch(grupoId, () => {
  if (!aniosDelGrupo.value.some((a) => a.id === anioProgramaId.value)) {
    anioProgramaId.value = aniosDelGrupo.value[0]?.id ?? null
  }
})

function abrirCrear() {
  grupoId.value = props.grupoInicialId ?? props.grupos[0]?.id ?? null
  anioProgramaId.value = aniosDelGrupo.value[0]?.id ?? null
  fechaOtorgada.value = ''
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !anioProgramaId.value || !fechaOtorgada.value) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  try {
    const creada = await props.otorgar(anioProgramaId.value, fechaOtorgada.value)
    medallas.value.push(creada)
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo otorgar la medalla.'
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
        Otorgar medalla
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
            <th class="px-4 py-3">Año de programa</th>
            <th class="px-4 py-3">Fecha otorgada</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="2" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="medallas.length === 0">
            <td colspan="2" class="px-4 py-6 text-center text-mk-text-muted">
              Sin medallas otorgadas todavía.
            </td>
          </tr>
          <tr v-for="m in medallas" v-else :key="m.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ etiquetaAnio(m.anioProgramaId) }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ m.fechaOtorgada }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <ModalDialog v-model="formOpen" title="Otorgar medalla">
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
            <option v-for="a in aniosDelGrupo" :key="a.id" :value="a.id">
              Año {{ a.numero }}{{ a.medalla ? ` · ${a.medalla}` : '' }}
            </option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Fecha otorgada</label>
          <input
            v-model="fechaOtorgada"
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
