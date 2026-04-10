package com.foss.vidoplay.presentation.common


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment


object GlassAnimations {

    val IndicatorEnter = fadeIn(tween(160)) + scaleIn(
        initialScale = 0.75f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh)
    )

    val IndicatorExit = fadeOut(tween(200)) + scaleOut(targetScale = 0.80f, animationSpec = tween(200))

    val PanelEnter = fadeIn(tween(250, easing = FastOutSlowInEasing)) + slideInVertically(
        initialOffsetY = { it / 2 }, animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    val PanelExit = fadeOut(tween(200, easing = FastOutSlowInEasing)) + slideOutVertically(
        targetOffsetY = { it / 2 }, animationSpec = tween(220, easing = FastOutSlowInEasing)
    )

    val TopBarEnter = fadeIn(tween(220)) + slideInVertically(
        initialOffsetY = { -it }, animationSpec = tween(280, easing = FastOutSlowInEasing)
    )

    val TopBarExit = fadeOut(tween(180)) + slideOutVertically(
        targetOffsetY = { -it }, animationSpec = tween(200, easing = FastOutSlowInEasing)
    )

    val SubMenuEnter = fadeIn(tween(200)) + expandVertically(
        expandFrom = Alignment.Bottom, animationSpec = tween(250, easing = FastOutSlowInEasing)
    )

    val SubMenuExit = fadeOut(tween(160)) + shrinkVertically(
        shrinkTowards = Alignment.Bottom, animationSpec = tween(200, easing = FastOutSlowInEasing)
    )

    val ToastEnter = fadeIn(tween(180)) + slideInVertically(
        initialOffsetY = { -it / 2 }, animationSpec = tween(220, easing = FastOutSlowInEasing)
    )

    val ToastExit = fadeOut(tween(200)) + slideOutVertically(
        targetOffsetY = { -it / 2 }, animationSpec = tween(220)
    )

    val OverlayEnter = fadeIn(tween(300))
    val OverlayExit = fadeOut(tween(250))

    val CenterControlsEnter = fadeIn(tween(200)) + scaleIn(
        initialScale = 0.80f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
    )

    val CenterControlsExit = fadeOut(tween(160)) + scaleOut(targetScale = 0.80f, animationSpec = tween(160))
}