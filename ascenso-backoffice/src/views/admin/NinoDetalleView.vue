<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDestacamentoStore } from '@/stores/destacamento.store'
import { ninoService } from '@/services/ninos/NinoService'
import { grupoService } from '@/services/catalogo/GrupoService'
import { anioProgramaService } from '@/services/catalogo/AnioProgramaService'
import { libroBiblicoService } from '@/services/catalogo/LibroBiblicoService'
import { destrezaService } from '@/services/catalogo/DestrezaService'
import { liderazgoService } from '@/services/catalogo/LiderazgoService'
import { pasoRequeridoService } from '@/services/catalogo/PasoRequeridoService'
import { progresoLibroService } from '@/services/ninos/ProgresoLibroService'
import { progresoDestrezaService } from '@/services/ninos/ProgresoDestrezaService'
import { progresoLiderazgoService } from '@/services/ninos/ProgresoLiderazgoService'
import { progresoRequisitoService } from '@/services/ninos/ProgresoRequisitoService'
import { medallaOtorgadaService } from '@/services/ninos/MedallaOtorgadaService'
import { logroMayorService } from '@/services/ninos/LogroMayorService'
import { ninoPadreService } from '@/services/actividades/NinoPadreService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Grupo, AnioPrograma, LibroBiblico, Destreza, Liderazgo, PasoRequerido } from '@/types/catalogo'
import type { Nino } from '@/types/ninos'
import ProgresoConAnioTab from '@/components/ninos/detalle/ProgresoConAnioTab.vue'
import ProgresoRequisitoTab from '@/components/ninos/detalle/ProgresoRequisitoTab.vue'
import MedallasTab from '@/components/ninos/detalle/MedallasTab.vue'
import LogroMayorTab from '@/components/ninos/detalle/LogroMayorTab.vue'
import PadresTab from '@/components/ninos/detalle/PadresTab.vue'

const TABS = [
  { id: 'libros', label: 'Progreso libros' },
  { id: 'destrezas', label: 'Progreso destrezas' },
  { id: 'liderazgo', label: 'Progreso liderazgo' },
  { id: 'requisitos', label: 'Pasos requeridos' },
  { id: 'medallas', label: 'Medallas' },
  { id: 'logro-mayor', label: 'Logro mayor' },
  { id: 'padres', label: 'Padres' },
] as const

type TabId = (typeof TABS)[number]['id']

const route = useRoute()
const router = useRouter()
const destacamentoStore = useDestacamentoStore()

const ninoId = computed(() => Number(route.params.id))
const tabActual = ref<TabId>('libros')

const nino = ref<Nino | null>(null)
const grupos = ref<Grupo[]>([])
const aniosPrograma = ref<AnioPrograma[]>([])
const librosBiblicos = ref<LibroBiblico[]>([])
const destrezas = ref<Destreza[]>([])
const liderazgos = ref<Liderazgo[]>([])
const pasosRequeridos = ref<PasoRequerido[]>([])

const cargando = ref(false)
const errorMessage = ref<string | null>(null)

function nombreGrupo(id: number): string {
  return grupos.value.find((g) => g.id === id)?.nombre ?? `Grupo #${id}`
}

function etiquetaAnioActual(id: number): string {
  const a = aniosPrograma.value.find((x) => x.id === id)
  return a ? `Año ${a.numero}${a.medalla ? ` · ${a.medalla}` : ''}` : `Año #${id}`
}

function calcularEdad(fechaNacimiento: string): number {
  const nacimiento = new Date(fechaNacimiento)
  const hoy = new Date()
  let edad = hoy.getFullYear() - nacimiento.getFullYear()
  const mesDiff = hoy.getMonth() - nacimiento.getMonth()
  if (mesDiff < 0 || (mesDiff === 0 && hoy.getDate() < nacimiento.getDate())) edad--
  return edad
}

onMounted(async () => {
  cargando.value = true
  errorMessage.value = null
  try {
    await destacamentoStore.cargar()
    const destacamentoId = destacamentoStore.actualId
    if (!destacamentoId) throw new Error('No hay destacamento seleccionado.')

    const [n, gruposResp, aniosResp, librosResp, destrezasResp, liderazgosResp, pasosResp] =
      await Promise.all([
        ninoService.obtener(destacamentoId, ninoId.value),
        grupoService.listar(),
        anioProgramaService.listar(),
        libroBiblicoService.listar(),
        destrezaService.listar(),
        liderazgoService.listar(),
        pasoRequeridoService.listar(),
      ])
    nino.value = n
    grupos.value = gruposResp.sort((a, b) => a.orden - b.orden)
    aniosPrograma.value = aniosResp
    librosBiblicos.value = librosResp
    destrezas.value = destrezasResp
    liderazgos.value = liderazgosResp
    pasosRequeridos.value = pasosResp
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar la ficha del niño.'
  } finally {
    cargando.value = false
  }
})

// --- Adaptadores: mapean el shape de cada Progreso* del backend (con su
// nombre de campo propio, ej. libroBiblicoId) al shape genérico que consume
// ProgresoConAnioTab (catalogoItemId) — ver ese componente para el porqué.
function listarLibros() {
  const destacamentoId = destacamentoStore.actualId as number
  return progresoLibroService.listar(destacamentoId, ninoId.value).then((r) =>
    r.map((x) => ({
      id: x.id,
      catalogoItemId: x.libroBiblicoId,
      anioProgramaObjetivoId: x.anioProgramaObjetivoId,
      fechaCompletado: x.fechaCompletado,
    })),
  )
}
function registrarLibro(catalogoItemId: number, anioProgramaObjetivoId: number, fechaCompletado: string) {
  const destacamentoId = destacamentoStore.actualId as number
  return progresoLibroService
    .registrar(destacamentoId, ninoId.value, {
      libroBiblicoId: catalogoItemId,
      anioProgramaObjetivoId,
      fechaCompletado,
    })
    .then((x) => ({
      id: x.id,
      catalogoItemId: x.libroBiblicoId,
      anioProgramaObjetivoId: x.anioProgramaObjetivoId,
      fechaCompletado: x.fechaCompletado,
    }))
}

function listarDestrezas() {
  const destacamentoId = destacamentoStore.actualId as number
  return progresoDestrezaService.listar(destacamentoId, ninoId.value).then((r) =>
    r.map((x) => ({
      id: x.id,
      catalogoItemId: x.destrezaId,
      anioProgramaObjetivoId: x.anioProgramaObjetivoId,
      fechaCompletado: x.fechaCompletado,
    })),
  )
}
function registrarDestreza(catalogoItemId: number, anioProgramaObjetivoId: number, fechaCompletado: string) {
  const destacamentoId = destacamentoStore.actualId as number
  return progresoDestrezaService
    .registrar(destacamentoId, ninoId.value, {
      destrezaId: catalogoItemId,
      anioProgramaObjetivoId,
      fechaCompletado,
    })
    .then((x) => ({
      id: x.id,
      catalogoItemId: x.destrezaId,
      anioProgramaObjetivoId: x.anioProgramaObjetivoId,
      fechaCompletado: x.fechaCompletado,
    }))
}

function listarLiderazgo() {
  const destacamentoId = destacamentoStore.actualId as number
  return progresoLiderazgoService.listar(destacamentoId, ninoId.value).then((r) =>
    r.map((x) => ({
      id: x.id,
      catalogoItemId: x.liderazgoId,
      anioProgramaObjetivoId: x.anioProgramaObjetivoId,
      fechaCompletado: x.fechaCompletado,
    })),
  )
}
function registrarLiderazgo(catalogoItemId: number, anioProgramaObjetivoId: number, fechaCompletado: string) {
  const destacamentoId = destacamentoStore.actualId as number
  return progresoLiderazgoService
    .registrar(destacamentoId, ninoId.value, {
      liderazgoId: catalogoItemId,
      anioProgramaObjetivoId,
      fechaCompletado,
    })
    .then((x) => ({
      id: x.id,
      catalogoItemId: x.liderazgoId,
      anioProgramaObjetivoId: x.anioProgramaObjetivoId,
      fechaCompletado: x.fechaCompletado,
    }))
}

function listarRequisitos() {
  const destacamentoId = destacamentoStore.actualId as number
  return progresoRequisitoService.listar(destacamentoId, ninoId.value)
}
function registrarRequisito(pasoRequeridoId: number, fechaCompletado: string) {
  const destacamentoId = destacamentoStore.actualId as number
  return progresoRequisitoService.registrar(destacamentoId, ninoId.value, {
    pasoRequeridoId,
    fechaCompletado,
  })
}

function listarMedallas() {
  const destacamentoId = destacamentoStore.actualId as number
  return medallaOtorgadaService.listar(destacamentoId, ninoId.value)
}
function otorgarMedalla(anioProgramaId: number, fechaOtorgada: string) {
  const destacamentoId = destacamentoStore.actualId as number
  return medallaOtorgadaService.otorgar(destacamentoId, ninoId.value, { anioProgramaId, fechaOtorgada })
}

function obtenerLogroMayor() {
  const destacamentoId = destacamentoStore.actualId as number
  return logroMayorService.obtener(destacamentoId, ninoId.value)
}
function otorgarLogroMayor(fechaOtorgada: string) {
  const destacamentoId = destacamentoStore.actualId as number
  return logroMayorService.otorgar(destacamentoId, ninoId.value, { fechaOtorgada })
}

function listarPadres() {
  const destacamentoId = destacamentoStore.actualId as number
  return ninoPadreService.listar(destacamentoId, ninoId.value)
}
function vincularPadre(usuarioId: number) {
  const destacamentoId = destacamentoStore.actualId as number
  return ninoPadreService.vincular(destacamentoId, ninoId.value, { usuarioId })
}
function desvincularPadre(usuarioId: number) {
  const destacamentoId = destacamentoStore.actualId as number
  return ninoPadreService.desvincular(destacamentoId, ninoId.value, usuarioId)
}

const librosItems = computed(() =>
  librosBiblicos.value.map((l) => ({ id: l.id, grupoId: l.grupoId, label: l.titulo })),
)
const destrezasItems = computed(() =>
  destrezas.value.map((d) => ({ id: d.id, grupoId: d.grupoId, label: d.nombre })),
)
const liderazgosItems = computed(() =>
  liderazgos.value.map((l) => ({ id: l.id, grupoId: l.grupoId, label: l.nombre })),
)
</script>

<template>
  <div class="space-y-4 p-6">
    <button type="button" class="mk-btn mk-btn-ghost -ml-2" @click="router.push({ name: 'ninos' })">
      ← Volver a niños
    </button>

    <p
      v-if="errorMessage"
      class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>
    <p v-else-if="cargando" class="text-sm text-mk-text-muted">Cargando…</p>

    <template v-else-if="nino">
      <div class="mk-card p-5">
        <div class="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h1 class="text-lg font-bold text-mk-text">{{ nino.nombreCompleto }}</h1>
            <p class="text-sm text-mk-text-muted">
              {{ calcularEdad(nino.fechaNacimiento) }} años · {{ nombreGrupo(nino.grupoActualId) }} ·
              {{ etiquetaAnioActual(nino.anioProgramaActualId) }}
            </p>
            <p class="text-sm text-mk-text-muted">
              Encargado: {{ nino.encargadoNombre }} ({{ nino.encargadoContacto }})
            </p>
          </div>
          <span class="mk-badge" :class="nino.activo ? 'mk-badge-success' : 'mk-badge-neutral'">
            {{ nino.activo ? 'Activo' : 'Inactivo' }}
          </span>
        </div>
      </div>

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

      <ProgresoConAnioTab
        v-if="tabActual === 'libros'"
        entidad-label="Libro"
        :grupos="grupos"
        :anios-programa="aniosPrograma"
        :grupo-inicial-id="nino.grupoActualId"
        :catalogo-items="librosItems"
        :listar="listarLibros"
        :registrar="registrarLibro"
      />

      <ProgresoConAnioTab
        v-else-if="tabActual === 'destrezas'"
        entidad-label="Destreza"
        :grupos="grupos"
        :anios-programa="aniosPrograma"
        :grupo-inicial-id="nino.grupoActualId"
        :catalogo-items="destrezasItems"
        :listar="listarDestrezas"
        :registrar="registrarDestreza"
      />

      <ProgresoConAnioTab
        v-else-if="tabActual === 'liderazgo'"
        entidad-label="Liderazgo"
        :grupos="grupos"
        :anios-programa="aniosPrograma"
        :grupo-inicial-id="nino.grupoActualId"
        :catalogo-items="liderazgosItems"
        :listar="listarLiderazgo"
        :registrar="registrarLiderazgo"
      />

      <ProgresoRequisitoTab
        v-else-if="tabActual === 'requisitos'"
        :grupos="grupos"
        :anios-programa="aniosPrograma"
        :grupo-inicial-id="nino.grupoActualId"
        :pasos-requeridos="pasosRequeridos"
        :listar="listarRequisitos"
        :registrar="registrarRequisito"
      />

      <MedallasTab
        v-else-if="tabActual === 'medallas'"
        :grupos="grupos"
        :anios-programa="aniosPrograma"
        :grupo-inicial-id="nino.grupoActualId"
        :listar="listarMedallas"
        :otorgar="otorgarMedalla"
      />

      <LogroMayorTab
        v-else-if="tabActual === 'logro-mayor'"
        :obtener="obtenerLogroMayor"
        :otorgar="otorgarLogroMayor"
      />

      <PadresTab
        v-else-if="tabActual === 'padres'"
        :listar="listarPadres"
        :vincular="vincularPadre"
        :desvincular="desvincularPadre"
      />
    </template>
  </div>
</template>
