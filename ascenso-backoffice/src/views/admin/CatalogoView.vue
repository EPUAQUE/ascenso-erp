<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { grupoService } from '@/services/catalogo/GrupoService'
import { anioProgramaService } from '@/services/catalogo/AnioProgramaService'
import { destrezaService } from '@/services/catalogo/DestrezaService'
import { liderazgoService } from '@/services/catalogo/LiderazgoService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma, Grupo } from '@/types/catalogo'
import GruposTab from '@/components/catalogo/GruposTab.vue'
import AniosProgramaTab from '@/components/catalogo/AniosProgramaTab.vue'
import LibrosBiblicosTab from '@/components/catalogo/LibrosBiblicosTab.vue'
import NombreCategoriaTab from '@/components/catalogo/NombreCategoriaTab.vue'
import PasosRequeridosTab from '@/components/catalogo/PasosRequeridosTab.vue'
import ReglasAsistenciaTab from '@/components/catalogo/ReglasAsistenciaTab.vue'

const TABS = [
  { id: 'grupos', label: 'Grupos' },
  { id: 'anios', label: 'Años de programa' },
  { id: 'libros', label: 'Libros bíblicos' },
  { id: 'destrezas', label: 'Destrezas' },
  { id: 'liderazgo', label: 'Liderazgo' },
  { id: 'pasos', label: 'Pasos requeridos' },
  { id: 'reglas', label: 'Reglas de asistencia' },
] as const

type TabId = (typeof TABS)[number]['id']

const tabActual = ref<TabId>('grupos')

const grupos = ref<Grupo[]>([])
const aniosPrograma = ref<AnioPrograma[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

async function cargarBase() {
  cargando.value = true
  errorMessage.value = null
  try {
    const [gruposResp, aniosResp] = await Promise.all([grupoService.listar(), anioProgramaService.listar()])
    grupos.value = gruposResp.sort((a, b) => a.orden - b.orden)
    aniosPrograma.value = aniosResp
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cargar el catálogo.'
  } finally {
    cargando.value = false
  }
}

onMounted(cargarBase)

function onGrupoActualizado(grupo: Grupo) {
  const idx = grupos.value.findIndex((g) => g.id === grupo.id)
  if (idx >= 0) grupos.value[idx] = grupo
}
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
      <GruposTab v-if="tabActual === 'grupos'" :grupos="grupos" @actualizado="onGrupoActualizado" />

      <AniosProgramaTab v-else-if="tabActual === 'anios'" :grupos="grupos" />

      <LibrosBiblicosTab v-else-if="tabActual === 'libros'" :grupos="grupos" />

      <NombreCategoriaTab
        v-else-if="tabActual === 'destrezas'"
        entidad-label="Destreza"
        campo-categoria-label="Categoría"
        :grupos="grupos"
        :listar="destrezaService.listar"
        :crear="destrezaService.crear"
        :actualizar="destrezaService.actualizar"
        :activar="destrezaService.activar"
        :desactivar="destrezaService.desactivar"
      />

      <NombreCategoriaTab
        v-else-if="tabActual === 'liderazgo'"
        entidad-label="Liderazgo"
        campo-categoria-label="Categoría"
        :grupos="grupos"
        :listar="liderazgoService.listar"
        :crear="liderazgoService.crear"
        :actualizar="liderazgoService.actualizar"
        :activar="liderazgoService.activar"
        :desactivar="liderazgoService.desactivar"
      />

      <PasosRequeridosTab
        v-else-if="tabActual === 'pasos'"
        :grupos="grupos"
        :anios-programa="aniosPrograma"
      />

      <ReglasAsistenciaTab v-else-if="tabActual === 'reglas'" />
    </template>
  </div>
</template>
