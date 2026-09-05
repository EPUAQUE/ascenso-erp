<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma, Grupo } from '@/types/catalogo'
import type { Nino, PromoverNinoRequest } from '@/types/ninos'

const props = defineProps<{
  modelValue: boolean
  nino: Nino | null
  grupos: Grupo[]
  aniosPrograma: AnioPrograma[]
  promover: (id: number, request: PromoverNinoRequest) => Promise<Nino>
}>()

const emit = defineEmits<{ (e: 'update:modelValue', value: boolean): void }>()

const grupoId = ref<number | null>(null)
const anioProgramaId = ref<number | null>(null)
const guardando = ref(false)
const errorMessage = ref<string | null>(null)

const aniosDelGrupo = computed(() =>
  props.aniosPrograma.filter((a) => a.grupoId === grupoId.value).sort((a, b) => a.numero - b.numero),
)

watch(
  () => props.modelValue,
  (abierto) => {
    if (!abierto) return
    grupoId.value = props.nino?.grupoActualId ?? props.grupos[0]?.id ?? null
    anioProgramaId.value = props.nino?.anioProgramaActualId ?? null
    errorMessage.value = null
  },
)

watch(grupoId, () => {
  if (!aniosDelGrupo.value.some((a) => a.id === anioProgramaId.value)) {
    anioProgramaId.value = aniosDelGrupo.value[0]?.id ?? null
  }
})

function etiquetaAnio(anio: AnioPrograma): string {
  const medalla = anio.medalla ? ` — medalla ${anio.medalla}` : ''
  return `Año ${anio.numero}${medalla}${anio.esAnioGracia ? ' (gracia)' : ''}`
}

async function onSubmit() {
  if (guardando.value || !props.nino || !grupoId.value || !anioProgramaId.value) return
  guardando.value = true
  errorMessage.value = null
  try {
    await props.promover(props.nino.id, { grupoId: grupoId.value, anioProgramaId: anioProgramaId.value })
    emit('update:modelValue', false)
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo promover al niño.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <ModalDialog
    :model-value="modelValue"
    title="Promover niño"
    @update:model-value="(v) => emit('update:modelValue', v)"
  >
    <form class="space-y-4" @submit.prevent="onSubmit">
      <p v-if="nino" class="text-sm text-mk-text-muted">
        {{ nino.nombreCompleto }}
      </p>

      <p
        v-if="errorMessage"
        class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
        role="alert"
      >
        {{ errorMessage }}
      </p>

      <div>
        <label class="mb-1 block text-sm font-medium text-mk-text">Nuevo grupo etario</label>
        <select
          v-model.number="grupoId"
          class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
        >
          <option v-for="g in grupos" :key="g.id" :value="g.id">{{ g.nombre }}</option>
        </select>
      </div>

      <div>
        <label class="mb-1 block text-sm font-medium text-mk-text">Nuevo año de programa</label>
        <select
          v-model.number="anioProgramaId"
          class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
        >
          <option v-for="a in aniosDelGrupo" :key="a.id" :value="a.id">{{ etiquetaAnio(a) }}</option>
        </select>
      </div>

      <div class="flex justify-end gap-2 pt-2">
        <button type="button" class="mk-btn mk-btn-ghost" @click="emit('update:modelValue', false)">
          Cancelar
        </button>
        <button type="submit" :disabled="guardando" class="mk-btn mk-btn-primary">
          {{ guardando ? 'Guardando…' : 'Promover' }}
        </button>
      </div>
    </form>
  </ModalDialog>
</template>
