package moiz.dev.mainapp.viewModel

import androidx.lifecycle.ViewModel
import moiz.dev.mainapp.dataClasses.DetectionBox

class DetectionViewModel : ViewModel() {
    private val _boxes = mutableListOf<DetectionBox>()
    val boxes: List<DetectionBox> get() = _boxes

    public fun updateBoxes(newBoxes: List<DetectionBox>) {
        _boxes.clear()
        _boxes.addAll(newBoxes)
    }
}