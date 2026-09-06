<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { grupoService } from '@/services/catalogo/GrupoService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Grupo } from '@/types/catalogo'

const props = defineProps<{ grupoId: number }>()

const TABS = [
  { name: 'grupo-anios', label: 'Años de programa' },
  { name: 'grupo-libros', label: 'Libros bíblicos' },
  { name: 'grupo-destrezas', label: 'Destrezas' },
  { name: 'grupo-liderazgo', label: 'Liderazgo' },
  { name: 'grupo-pasos', label: 'Pasos requeridos' },
] as const

const grupo = ref<Grupo | null>(null)
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    grupo.value = await grupoService.obtener(props.grupoId)
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cargar el grupo.'
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)
watch(() => props.grupoId, cargar)
</script>

<template>
  <div class="space-y-4">
    <RouterLink :to="{ name: 'grupos' }" class="text-sm text-mk-text-muted hover:text-mk-text">
      ← Volver a grupos
    </RouterLink>

    <p
      v-if="errorMessage"
      class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>
    <div v-else-if="cargando" class="text-sm text-mk-text-muted">Cargando…</div>
    <div v-else-if="grupo" class="mk-card px-4 py-3">
      <p class="font-bold text-mk-text">{{ grupo.nombre }}</p>
      <p class="text-sm text-mk-text-muted">{{ grupo.edadMin }}–{{ grupo.edadMax }} años</p>
    </div>

    <div class="mk-card overflow-x-auto">
      <nav class="mk-scroll-x flex gap-1 p-1.5">
        <RouterLink
          v-for="tab in TABS"
          :key="tab.name"
          :to="{ name: tab.name, params: { grupoId } }"
          class="shrink-0 rounded-md px-3 py-1.5 text-sm font-medium text-mk-text-muted transition-colors hover:bg-mk-surface-2 hover:text-mk-text"
          active-class="!bg-mk-nav-active !text-mk-nav-active-ink"
        >
          {{ tab.label }}
        </RouterLink>
      </nav>
    </div>

    <RouterView />
  </div>
</template>
