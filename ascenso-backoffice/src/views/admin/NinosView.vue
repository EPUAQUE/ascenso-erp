<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useDestacamentoStore } from '@/stores/destacamento.store'
import { usePermissionsStore } from '@/stores/permissions.store'
import { ninoService } from '@/services/ninos/NinoService'
import { grupoService } from '@/services/catalogo/GrupoService'
import { anioProgramaService } from '@/services/catalogo/AnioProgramaService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Grupo, AnioPrograma } from '@/types/catalogo'
import type { ActualizarNinoRequest, CrearNinoRequest, Nino, PromoverNinoRequest } from '@/types/ninos'
import EstadoBadge from '@/components/common/EstadoBadge.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import PaginacionTabla from '@/components/common/PaginacionTabla.vue'
import NinoFormModal from '@/components/ninos/NinoFormModal.vue'
import PromoverNinoModal from '@/components/ninos/PromoverNinoModal.vue'

const PAGE_SIZE = 10

const router = useRouter()
const destacamentoStore = useDestacamentoStore()
const permissions = usePermissionsStore()

const puedeEditar = computed(() => permissions.can('NINOS_EDITAR'))

const ninos = ref<Nino[]>([])
const grupos = ref<Grupo[]>([])
const aniosPrograma = ref<AnioPrograma[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

const busqueda = ref('')
const filtroEstado = ref<'todos' | 'activos' | 'inactivos'>('activos')
const pagina = ref(1)

const formOpen = ref(false)
const ninoEnEdicion = ref<Nino | null>(null)
const promoverOpen = ref(false)
const ninoAPromover = ref<Nino | null>(null)

const ninosFiltrados = computed(() => {
  const q = busqueda.value.trim().toLowerCase()
  return ninos.value.filter((n) => {
    if (filtroEstado.value === 'activos' && !n.activo) return false
    if (filtroEstado.value === 'inactivos' && n.activo) return false
    if (q && !n.nombreCompleto.toLowerCase().includes(q)) return false
    return true
  })
})

const totalPaginas = computed(() => Math.max(1, Math.ceil(ninosFiltrados.value.length / PAGE_SIZE)))

const ninosPagina = computed(() => {
  const inicio = (pagina.value - 1) * PAGE_SIZE
  return ninosFiltrados.value.slice(inicio, inicio + PAGE_SIZE)
})

watch([busqueda, filtroEstado], () => {
  pagina.value = 1
})

function nombreGrupo(grupoId: number): string {
  return grupos.value.find((g) => g.id === grupoId)?.nombre ?? `Grupo #${grupoId}`
}

function etiquetaAnioPrograma(anioProgramaId: number): string {
  const anio = aniosPrograma.value.find((a) => a.id === anioProgramaId)
  if (!anio) return `Año #${anioProgramaId}`
  return anio.medalla ? `Año ${anio.numero} · ${anio.medalla}` : `Año ${anio.numero}`
}

function calcularEdad(fechaNacimiento: string): number {
  const nacimiento = new Date(fechaNacimiento)
  const hoy = new Date()
  let edad = hoy.getFullYear() - nacimiento.getFullYear()
  const mesDiff = hoy.getMonth() - nacimiento.getMonth()
  if (mesDiff < 0 || (mesDiff === 0 && hoy.getDate() < nacimiento.getDate())) edad--
  return edad
}

async function cargarCatalogo() {
  const [gruposResp, aniosResp] = await Promise.all([grupoService.listar(), anioProgramaService.listar()])
  grupos.value = gruposResp.sort((a, b) => a.orden - b.orden)
  aniosPrograma.value = aniosResp
}

async function cargarNinos() {
  const destacamentoId = destacamentoStore.actualId
  if (!destacamentoId) {
    ninos.value = []
    return
  }
  cargando.value = true
  errorMessage.value = null
  try {
    ninos.value = await ninoService.listar(destacamentoId)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar la lista de niños.'
  } finally {
    cargando.value = false
  }
}

onMounted(async () => {
  errorMessage.value = null
  try {
    await destacamentoStore.cargar()
    await cargarCatalogo()
    await cargarNinos()
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar la información inicial.'
  }
})

watch(
  () => destacamentoStore.actualId,
  () => {
    pagina.value = 1
    cargarNinos()
  },
)

function reemplazarNino(actualizado: Nino) {
  const idx = ninos.value.findIndex((n) => n.id === actualizado.id)
  if (idx >= 0) ninos.value[idx] = actualizado
}

function abrirCrear() {
  ninoEnEdicion.value = null
  formOpen.value = true
}

function abrirEditar(n: Nino) {
  ninoEnEdicion.value = n
  formOpen.value = true
}

function verFicha(n: Nino) {
  router.push({ name: 'nino-detalle', params: { id: n.id } })
}

function abrirPromover(n: Nino) {
  ninoAPromover.value = n
  promoverOpen.value = true
}

async function onCrear(request: CrearNinoRequest): Promise<Nino> {
  const destacamentoId = destacamentoStore.actualId
  if (!destacamentoId) throw new Error('No hay destacamento seleccionado.')
  const creado = await ninoService.crear(destacamentoId, request)
  ninos.value.unshift(creado)
  return creado
}

async function onActualizar(id: number, request: ActualizarNinoRequest): Promise<Nino> {
  const destacamentoId = destacamentoStore.actualId
  if (!destacamentoId) throw new Error('No hay destacamento seleccionado.')
  const actualizado = await ninoService.actualizar(destacamentoId, id, request)
  reemplazarNino(actualizado)
  return actualizado
}

async function onPromover(id: number, request: PromoverNinoRequest): Promise<Nino> {
  const destacamentoId = destacamentoStore.actualId
  if (!destacamentoId) throw new Error('No hay destacamento seleccionado.')
  const actualizado = await ninoService.promover(destacamentoId, id, request)
  reemplazarNino(actualizado)
  return actualizado
}

async function onToggleActivo(n: Nino) {
  const destacamentoId = destacamentoStore.actualId
  if (!destacamentoId) return
  errorMessage.value = null
  try {
    if (n.activo) {
      await ninoService.desactivar(destacamentoId, n.id)
    } else {
      await ninoService.activar(destacamentoId, n.id)
    }
    n.activo = !n.activo
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cambiar el estado del niño.'
  }
}
</script>

<template>
  <div class="space-y-4 p-6">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <div class="flex flex-wrap items-center gap-3">
        <label v-if="destacamentoStore.opciones.length > 1" class="flex items-center gap-2 text-sm">
          <span class="text-mk-text-muted">Destacamento</span>
          <select
            :value="destacamentoStore.actualId ?? undefined"
            class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
            @change="destacamentoStore.seleccionar(Number(($event.target as HTMLSelectElement).value))"
          >
            <option v-for="o in destacamentoStore.opciones" :key="o.id" :value="o.id">{{ o.nombre }}</option>
          </select>
        </label>
        <span v-else-if="destacamentoStore.opciones.length === 1" class="text-sm text-mk-text-muted">
          {{ destacamentoStore.opciones[0].nombre }}
        </span>

        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar por nombre…"
          class="mk-input w-56 rounded-md border border-mk-border px-3 py-1.5 text-sm"
        />

        <select
          v-model="filtroEstado"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
        >
          <option value="activos">Activos</option>
          <option value="inactivos">Inactivos</option>
          <option value="todos">Todos</option>
        </select>
      </div>

      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo niño
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
            <th class="px-4 py-3">Nombre</th>
            <th class="px-4 py-3">Edad</th>
            <th class="px-4 py-3">Grupo actual</th>
            <th class="px-4 py-3">Año de programa</th>
            <th class="px-4 py-3">Encargado</th>
            <th class="px-4 py-3">Estado</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="7" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="ninosPagina.length === 0">
            <td colspan="7" class="px-4 py-6 text-center text-mk-text-muted">No hay niños para mostrar.</td>
          </tr>
          <tr v-for="n in ninosPagina" v-else :key="n.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ n.nombreCompleto }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ calcularEdad(n.fechaNacimiento) }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ nombreGrupo(n.grupoActualId) }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ etiquetaAnioPrograma(n.anioProgramaActualId) }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">
              <div>{{ n.encargadoNombre }}</div>
              <div class="text-xs">{{ n.encargadoContacto }}</div>
            </td>
            <td class="px-4 py-2.5">
              <EstadoBadge
                :variant="n.activo ? 'success' : 'neutral'"
                :label="n.activo ? 'Activo' : 'Inactivo'"
              />
            </td>
            <td class="px-4 py-2.5">
              <div class="mk-row-actions justify-end">
                <button
                  type="button"
                  class="mk-row-btn mk-row-btn-neutral"
                  title="Ver ficha"
                  @click="verFicha(n)"
                >
                  <ActionIcon name="eye" />
                </button>
                <template v-if="puedeEditar">
                  <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(n)">
                    <ActionIcon name="edit" />
                  </button>
                  <button
                    type="button"
                    class="mk-row-btn mk-row-btn-success"
                    title="Promover"
                    @click="abrirPromover(n)"
                  >
                    <ActionIcon name="arrow-up" />
                  </button>
                  <button
                    type="button"
                    class="mk-row-btn"
                    :class="n.activo ? 'mk-row-btn-danger' : 'mk-row-btn-success'"
                    :title="n.activo ? 'Desactivar' : 'Activar'"
                    @click="onToggleActivo(n)"
                  >
                    <ActionIcon name="power" />
                  </button>
                </template>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="totalPaginas > 1" class="flex justify-end">
      <PaginacionTabla v-model:pagina="pagina" :total-paginas="totalPaginas" />
    </div>

    <NinoFormModal
      v-model="formOpen"
      :nino="ninoEnEdicion"
      :grupos="grupos"
      :anios-programa="aniosPrograma"
      :crear="onCrear"
      :actualizar="onActualizar"
    />

    <PromoverNinoModal
      v-model="promoverOpen"
      :nino="ninoAPromover"
      :grupos="grupos"
      :anios-programa="aniosPrograma"
      :promover="onPromover"
    />
  </div>
</template>
