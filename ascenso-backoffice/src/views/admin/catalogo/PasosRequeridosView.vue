<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { anioProgramaService } from '@/services/catalogo/AnioProgramaService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma } from '@/types/catalogo'
import PasosRequeridosTab from '@/components/catalogo/PasosRequeridosTab.vue'

const props = defineProps<{ grupoId: number }>()

const aniosPrograma = ref<AnioPrograma[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    aniosPrograma.value = await anioProgramaService.listar(props.grupoId)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar los años de programa.'
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)
watch(() => props.grupoId, cargar)
</script>

<template>
  <p
    v-if="errorMessage"
    class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
    role="alert"
  >
    {{ errorMessage }}
  </p>
  <p v-else-if="cargando" class="text-sm text-mk-text-muted">Cargando…</p>
  <PasosRequeridosTab v-else :anios-programa="aniosPrograma" />
</template>
