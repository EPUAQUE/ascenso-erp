<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useDestacamentoStore } from '@/stores/destacamento.store'
import { trimestreService } from '@/services/ninos/TrimestreService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Trimestre } from '@/types/ninos'
import TomarAsistenciaTab from '@/components/asistencia/TomarAsistenciaTab.vue'
import TrimestresTab from '@/components/asistencia/TrimestresTab.vue'

const TABS = [
  { id: 'tomar', label: 'Tomar asistencia' },
  { id: 'trimestres', label: 'Trimestres' },
] as const

type TabId = (typeof TABS)[number]['id']

const tabActual = ref<TabId>('tomar')

const destacamentoStore = useDestacamentoStore()
const trimestres = ref<Trimestre[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

async function cargarTrimestres() {
  try {
    trimestres.value = await trimestreService.listar()
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cargar los trimestres.'
  }
}

onMounted(async () => {
  cargando.value = true
  errorMessage.value = null
  try {
    await destacamentoStore.cargar()
    await cargarTrimestres()
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar la información inicial.'
  } finally {
    cargando.value = false
  }
})
</script>

<template>
  <div class="space-y-4 p-6">
    <div class="mk-card overflow-x-auto">
      <nav class="mk-scroll-x flex gap-1 p-1.5">
        <button
          v-for="tab in TABS"
          :key="tab.id"
          type="button"
          class="shrink-0 rounded-md px-3 py-1.5 text-sm font-medium transition-colors"
          :class="
            tab.id === tabActual
              ? 'bg-mk-nav-active text-mk-nav-active-ink'
              : 'text-mk-text-muted hover:bg-mk-surface-2 hover:text-mk-text'
          "
          @click="tabActual = tab.id"
        >
          {{ tab.label }}
        </button>
      </nav>
    </div>

    <p
      v-if="errorMessage"
      class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>
    <p v-else-if="cargando" class="text-sm text-mk-text-muted">Cargando…</p>

    <template v-else>
      <TomarAsistenciaTab v-if="tabActual === 'tomar'" :trimestres="trimestres" />
      <TrimestresTab
        v-else-if="tabActual === 'trimestres'"
        :trimestres="trimestres"
        @cambiado="cargarTrimestres"
      />
    </template>
  </div>
</template>
