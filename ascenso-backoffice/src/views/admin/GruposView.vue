<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { grupoService } from '@/services/catalogo/GrupoService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Grupo } from '@/types/catalogo'
import GruposTab from '@/components/catalogo/GruposTab.vue'

const grupos = ref<Grupo[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    grupos.value = (await grupoService.listar()).sort((a, b) => a.orden - b.orden)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar el catálogo de grupos.'
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)

function onActualizado(grupo: Grupo) {
  const idx = grupos.value.findIndex((g) => g.id === grupo.id)
  if (idx >= 0) grupos.value[idx] = grupo
}
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
    <GruposTab v-else :grupos="grupos" @actualizado="onActualizado" />
  </div>
</template>
