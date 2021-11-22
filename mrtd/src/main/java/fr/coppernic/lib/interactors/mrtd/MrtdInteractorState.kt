package fr.coppernic.lib.interactors.mrtd

sealed class MrtdInteractorState {
    object MrtdReadStarted : MrtdInteractorState()
    data class MrtdReadDone(val dataGroup: DataGroup): MrtdInteractorState()
}