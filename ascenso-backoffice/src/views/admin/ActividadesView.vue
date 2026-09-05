<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useDestacamentoStore } from '@/stores/destacamento.store'
import { ApiClientError } from '@/services/http/ApiClient'
import ActividadesTab from '@/components/actividades/ActividadesTab.vue'
import AnunciosTab from '@/components/actividades/AnunciosTab.vue'

const TABS = [
  { id: 'actividades', label: 'Actividades' },
  { id: 'anuncios', label: 'Anuncios' },
] as const

type TabId = (typeof TABS)[number]['id']

const tabActual = ref<TabId>('actividades')

const destacamentoStore = useDestacamentoStore()
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

onMounted(async () => {
  cargando.value = true
  errorMessage.value = null
  try {
    await destacamentoStore.cargar()
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
              ? 'bg-mk-primary text-mk-primary-ink'
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
      <ActividadesTab v-if="tabActual === 'actividades'" />
      <AnunciosTab v-else-if="tabActual === 'anuncios'" />
    </template>
  </div>
</template>
