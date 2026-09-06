<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useDestacamentoStore } from '@/stores/destacamento.store'
import { ApiClientError } from '@/services/http/ApiClient'
import TomarAsistenciaTab from '@/components/asistencia/TomarAsistenciaTab.vue'

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
    <p
      v-if="errorMessage"
      class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>
    <p v-else-if="cargando" class="text-sm text-mk-text-muted">Cargando…</p>
    <TomarAsistenciaTab v-else />
  </div>
</template>
