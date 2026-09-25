package com.example.todoapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.todoapp.model.TodoTag

@Immutable
data class TagColors(
    val study: Color,
    val work: Color,
    val health: Color,
    val hobby: Color,
    val shopping: Color
) {
    fun colorFor(tag: TodoTag): Color = when (tag) {
        TodoTag.STUDY -> study
        TodoTag.WORK -> work
        TodoTag.HEALTH -> health
        TodoTag.HOBBY -> hobby
        TodoTag.SHOPPING -> shopping
    }
}

internal val LightTagColors = TagColors(
    study = Color(0xFF376A9A),
    work = Color(0xFF47734E),
    health = Color(0xFFA7524E),
    hobby = Color(0xFF835691),
    shopping = Color(0xFF75632A)
)

internal val DarkTagColors = TagColors(
    study = Color(0xFFD1E4FF),
    work = Color(0xFFB4F2BE),
    health = Color(0xFFFFDAD6),
    hobby = Color(0xFFF5D9FF),
    shopping = Color(0xFFFFF0AD)
)


internal val LocalTagColors = staticCompositionLocalOf<TagColors> {
    error("TagColors are not provided")
}

object TodoAppTheme {
    val tagColors: TagColors
    @Composable
    @ReadOnlyComposable
    get() = LocalTagColors.current
}
