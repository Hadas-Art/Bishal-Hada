package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TenseScore
import com.example.data.UserStatsRecord
import com.example.model.EnglishTense
import com.example.model.QuizQuestion
import com.example.model.PracticeMode
import com.example.viewmodel.TenseAppViewModel
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

// Sleek Interface Theme Color Palette
val SoftCream = Color(0xFFFDF8FF)       // Sleek light background
val DeepNavy = Color(0xFF21005D)        // Dark purple text/accents
val OchreGold = Color(0xFF6750A4)       // Primary core purple
val AccentOrange = Color(0xFFD0BCFF)    // Vibrant button purple accent
val LightIvory = Color(0xFFFFFFFF)      // Pure white card background
val CharcoalDark = Color(0xFF1C1B1F)    // Body dark charcoal
val SuccessGreen = Color(0xFF2E7D32)    // Correct match
val ErrorRed = Color(0xFFC62828)        // Incorrect match
val WarmGrey = Color(0xFFF3EDF7)        // Sleek surface container background
val SlateBorder = Color(0xFFCAC4D0)     // Sleek M3 divider border
val SleekPillBg = Color(0xFFEADDFF)     // Pill active color

// Robust, thread-safe system synthesizer for learning feedback beeps
object SoundManager {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            Log.e("SoundManager", "Failed to initialize ToneGenerator", e)
        }
    }

    fun playSuccess() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 150)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing success sound", e)
        }
    }

    fun playFailure() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 220)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing failure sound", e)
        }
    }
}

// Particle representation for highly optimized, gravity-aware responsive confetti vectors
data class ConfettiParticle(
    val id: Int,
    val rx: Float, // Relative X coordinate (0.0 to 1.0)
    val ry: Float, // Relative Y coordinate (0.0 to 1.0)
    val size: Float, // Size in pixels
    val color: Color,
    val rvx: Float, // Relative velocity X
    val rvy: Float, // Relative velocity Y
    val alpha: Float = 1f,
    val rotation: Float = 0f,
    val rotationSpeed: Float = 0f
)

@Composable
fun ConfettiOverlay(
    particles: List<ConfettiParticle>,
    modifier: Modifier = Modifier
) {
    if (particles.isNotEmpty()) {
        Canvas(modifier = modifier.fillMaxSize()) {
            particles.forEach { p ->
                val px = p.rx * size.width
                val py = p.ry * size.height
                
                drawContext.canvas.save()
                drawContext.canvas.translate(px, py)
                drawContext.canvas.rotate(p.rotation)
                drawRect(
                    color = p.color.copy(alpha = p.alpha),
                    topLeft = Offset(-p.size / 2f, -p.size / 2f),
                    size = androidx.compose.ui.geometry.Size(p.size, p.size)
                )
                drawContext.canvas.restore()
            }
        }
    }
}

@Composable
fun TenseBuddyMascot(
    isAnswered: Boolean,
    isCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    // Infinite transition for responsive physical idle breathing/floating dynamics
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val idleFloat by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    val eyeBlink by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3500
                1f at 0
                1f at 3200
                0.1f at 3300 // organic quick blink
                1f at 3400
                1f at 3500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    // Layout animatables for active bouncy transitions on answering correctly/incorrectly
    val transitionState = remember { Animatable(0f) }
    val rotationState = remember { Animatable(0f) }

    LaunchedEffect(isAnswered, isCorrect) {
        if (isAnswered) {
            if (isCorrect) {
                // Happy high-frequency jump
                transitionState.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium)
                )
            } else {
                // Disappointed side-to-side shake
                rotationState.animateTo(12f, spring(stiffness = Spring.StiffnessHigh))
                rotationState.animateTo(-12f, spring(stiffness = Spring.StiffnessHigh))
                rotationState.animateTo(6f, spring(stiffness = Spring.StiffnessHigh))
                rotationState.animateTo(-6f, spring(stiffness = Spring.StiffnessHigh))
                rotationState.animateTo(0f, spring(stiffness = Spring.StiffnessHigh))
            }
        } else {
            // Smoothly reset back to neutral idle
            transitionState.animateTo(0f)
            rotationState.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
            .size(110.dp)
            .graphicsLayer {
                // Apply subtle translations & scales
                translationY = if (isAnswered && isCorrect) -transitionState.value * 12.dp.toPx() else idleFloat.dp.toPx()
                rotationZ = rotationState.value
                val scale = if (isAnswered && isCorrect) 1f + (transitionState.value * 0.15f) else 1f
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = this.center
            val r = size.minDimension / 2.3f

            // Aura ring corresponding to feedback status
            val auraColor = when {
                !isAnswered -> OchreGold.copy(alpha = 0.08f)
                isCorrect -> SuccessGreen.copy(alpha = 0.15f)
                else -> ErrorRed.copy(alpha = 0.12f)
            }
            drawCircle(color = auraColor, radius = r * 1.25f)

            // Dynamic gradients for the mascot's core body
            val bodyBrush = Brush.linearGradient(
                colors = if (isAnswered && isCorrect) {
                    listOf(Color(0xFF81C784), Color(0xFF2E7D32))
                } else if (isAnswered) {
                    listOf(Color(0xFFE57373), Color(0xFFC62828))
                } else {
                    listOf(OchreGold, DeepNavy)
                },
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
            drawCircle(brush = bodyBrush, radius = r)

            // Sleek outer stroke rim decoration
            val rimColor = when {
                !isAnswered -> OchreGold
                isCorrect -> SuccessGreen
                else -> ErrorRed
            }
            drawCircle(color = rimColor, radius = r, style = Stroke(width = 3.dp.toPx()))

            // Screen faceplate canvas
            val faceRadius = r * 0.8f
            drawCircle(color = CharcoalDark, radius = faceRadius)

            // Robot head antenna details
            drawLine(
                color = rimColor,
                start = Offset(center.x, center.y - r),
                end = Offset(center.x, center.y - r - 12.dp.toPx()),
                strokeWidth = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            val antennaBallGlow = when {
                !isAnswered -> AccentOrange
                isCorrect -> Color(0xFF81C784)
                else -> Color(0xFFE57373)
            }
            drawCircle(
                color = antennaBallGlow,
                radius = 6.dp.toPx(),
                center = Offset(center.x, center.y - r - 15.dp.toPx())
            )

            // Facial expression placements
            val eyeSpacing = 16.dp.toPx()
            val eyeY = center.y - 6.dp.toPx()
            val leftEyeCenter = Offset(center.x - eyeSpacing, eyeY)
            val rightEyeCenter = Offset(center.x + eyeSpacing, eyeY)

            when {
                !isAnswered -> {
                    // Friendly blinking digital eyes
                    val eyeRadius = 6.dp.toPx()
                    drawCircle(
                        color = Color.Cyan,
                        radius = eyeRadius,
                        center = leftEyeCenter.copy(y = leftEyeCenter.y + (1f - eyeBlink) * eyeRadius)
                    )
                    drawCircle(
                        color = Color.Cyan,
                        radius = eyeRadius,
                        center = rightEyeCenter.copy(y = rightEyeCenter.y + (1f - eyeBlink) * eyeRadius)
                    )

                    // Smile vector path
                    val mouthPath = Path().apply {
                        moveTo(center.x - 7.dp.toPx(), center.y + 8.dp.toPx())
                        quadraticTo(
                            center.x, center.y + 14.dp.toPx(),
                            center.x + 7.dp.toPx(), center.y + 8.dp.toPx()
                        )
                    }
                    drawPath(
                        path = mouthPath,
                        color = Color.White.copy(alpha = 0.9f),
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                isCorrect -> {
                    // Star expressions
                    val starSize = 8.dp.toPx()
                    drawHappyFaceStar(leftEyeCenter, starSize)
                    drawHappyFaceStar(rightEyeCenter, starSize)

                    // Exuberant wide open joyful mouth
                    val mouthRect = Rect(center.x - 10.dp.toPx(), center.y + 1.dp.toPx(), center.x + 10.dp.toPx(), center.y + 14.dp.toPx())
                    val mouthPath = Path().apply {
                        arcTo(mouthRect, 0f, 180f, true)
                        lineTo(center.x - 10.dp.toPx(), center.y + 1.dp.toPx())
                        close()
                    }
                    drawPath(path = mouthPath, color = Color(0xFFFFD54F))
                }
                else -> {
                    // Dizzy cross eyes
                    val crossSize = 5.dp.toPx()
                    drawDizzyCross(leftEyeCenter, crossSize)
                    drawDizzyCross(rightEyeCenter, crossSize)

                    // Dizzy wavy mouth line
                    val mouthPath = Path().apply {
                        moveTo(center.x - 8.dp.toPx(), center.y + 11.dp.toPx())
                        quadraticTo(center.x - 4.dp.toPx(), center.y + 7.dp.toPx(), center.x, center.y + 11.dp.toPx())
                        quadraticTo(center.x + 4.dp.toPx(), center.y + 15.dp.toPx(), center.x + 8.dp.toPx(), center.y + 11.dp.toPx())
                    }
                    drawPath(
                        path = mouthPath,
                        color = Color.White,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawHappyFaceStar(center: Offset, size: Float) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        lineTo(center.x + size * 0.3f, center.y - size * 0.3f)
        lineTo(center.x + size, center.y)
        lineTo(center.x + size * 0.3f, center.y + size * 0.3f)
        lineTo(center.x, center.y + size)
        lineTo(center.x - size * 0.3f, center.y + size * 0.3f)
        lineTo(center.x - size, center.y)
        lineTo(center.x - size * 0.3f, center.y - size * 0.3f)
        close()
    }
    drawPath(path = path, color = Color(0xFFFFD54F))
}

private fun DrawScope.drawDizzyCross(center: Offset, size: Float) {
    drawLine(
        color = Color(0xFFEF5350),
        start = Offset(center.x - size, center.y - size),
        end = Offset(center.x + size, center.y + size),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFFEF5350),
        start = Offset(center.x + size, center.y - size),
        end = Offset(center.x - size, center.y + size),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TenseAppScreen(
    viewModel: TenseAppViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("quiz") } // "quiz", "guide", "mastery"

    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val tenseScores by viewModel.tenseScores.collectAsStateWithLifecycle()

    // Fully adaptive Scaffold handling Edge-to-Edge & System Navigation respects
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(SoftCream),
        topBar = {
            HeaderBar(userStats = userStats)
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .fillMaxWidth()
                    .height(78.dp)
                    .background(WarmGrey)
                    .drawBehind {
                        drawLine(
                            color = SlateBorder,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SoftCream, WarmGrey),
                        startY = 0f
                    )
                )
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    slideInVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        initialOffsetY = { 300 }
                    ) + fadeIn() with slideOutVertically(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        targetOffsetY = { -300 }
                    ) + fadeOut()
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    "quiz" -> PracticeScreen(viewModel = viewModel)
                    "guide" -> StudyGuideScreen()
                    "mastery" -> MasteryScreen(
                        userStats = userStats,
                        tenseScores = tenseScores,
                        onResetStats = { viewModel.resetStats() }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderBar(userStats: UserStatsRecord) {
    Column(
        modifier = Modifier
            .background(SoftCream)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sleek Initial Circle Box resembling Tailwind HTML: bg-[#EADDFF]
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SleekPillBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "H", // H for Hadas (User's Email)
                        color = DeepNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Master Tenses",
                        color = CharcoalDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Level: Intermediate B2",
                        color = Color(0xFF49454F),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Streak & Score pills
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Streak Card
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarmGrey)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🔥", fontSize = 13.sp)
                        Text(
                            text = "${userStats.streakCount} D",
                            color = DeepNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Total Score Card
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekPillBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Score",
                            tint = DeepNavy,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${userStats.totalScore} pts",
                            color = DeepNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PracticeScreen(viewModel: TenseAppViewModel) {
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()
    val currentTense by viewModel.selectedTense.collectAsStateWithLifecycle()
    val currentDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val currentQuestion by viewModel.currentQuestion.collectAsStateWithLifecycle()
    val selectedAnswer by viewModel.selectedAnswer.collectAsStateWithLifecycle()
    val isAnswered by viewModel.isAnswered.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    val shakeOffset = remember { Animatable(0f) }
    val confettiParticles = remember { mutableStateListOf<ConfettiParticle>() }

    LaunchedEffect(isAnswered, selectedAnswer) {
        if (isAnswered) {
            val isCorrect = selectedAnswer == currentQuestion?.correctValue
            if (isCorrect) {
                // Synthesize pleasant learning double confirm chime
                SoundManager.playSuccess()

                // Spurt physics-based celebratory particles
                val colors = listOf(
                    Color(0xFF6750A4), // OchrePurple
                    Color(0xFF2E7D32), // SuccessGreen
                    Color(0xFFFFD54F), // Amber
                    Color(0xFF03A9F4), // LightBlue
                    Color(0xFFE040FB), // Fuchsia
                    Color(0xFFFF5722)  // Orange
                )
                confettiParticles.clear()
                repeat(55) { id ->
                    confettiParticles.add(
                        ConfettiParticle(
                            id = id,
                            rx = 0.5f + (kotlin.random.Random.nextFloat() - 0.5f) * 0.15f,
                            ry = 0.3f, // Sprout near Sparky Mascot height
                            size = (14 + kotlin.random.Random.nextInt(18)).toFloat(),
                            color = colors.random(),
                            rvx = (kotlin.random.Random.nextFloat() - 0.5f) * 0.06f,
                            rvy = -0.018f - kotlin.random.Random.nextFloat() * 0.022f,
                            rotation = kotlin.random.Random.nextFloat() * 360f,
                            rotationSpeed = (kotlin.random.Random.nextFloat() - 0.5f) * 12f
                        )
                    )
                }

                val startTime = System.currentTimeMillis()
                while (confettiParticles.isNotEmpty() && System.currentTimeMillis() - startTime < 1800) {
                    withFrameMillis { frameTime ->
                        for (i in confettiParticles.indices.reversed()) {
                            val p = confettiParticles[i]
                            val updated = p.copy(
                                rx = p.rx + p.rvx,
                                ry = p.ry + p.rvy,
                                rvy = p.rvy + 0.0007f, // gravity pull down
                                alpha = maxOf(0f, p.alpha - 0.014f),
                                rotation = p.rotation + p.rotationSpeed
                            )
                            if (updated.alpha <= 0f || updated.ry > 1.0f) {
                                confettiParticles.removeAt(i)
                            } else {
                                confettiParticles[i] = updated
                            }
                        }
                    }
                }
            } else {
                // Play incorrect buzz, shake card left to right
                SoundManager.playFailure()
                
                shakeOffset.animateTo(22f, spring(stiffness = Spring.StiffnessHigh))
                shakeOffset.animateTo(-22f, spring(stiffness = Spring.StiffnessHigh))
                shakeOffset.animateTo(14f, spring(stiffness = Spring.StiffnessHigh))
                shakeOffset.animateTo(-14f, spring(stiffness = Spring.StiffnessHigh))
                shakeOffset.animateTo(7f, spring(stiffness = Spring.StiffnessHigh))
                shakeOffset.animateTo(-7f, spring(stiffness = Spring.StiffnessHigh))
                shakeOffset.animateTo(0f, spring(stiffness = Spring.StiffnessHigh))
            }
        } else {
            confettiParticles.clear()
            shakeOffset.snapTo(0f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Selector for Quiz Modes
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = " SELECT PRACTICE MODE",
                color = DeepNavy,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val mode1 = PracticeMode.SINGLE_TENSE
                    val mode2 = PracticeMode.MIXED
                    ModeCard(
                        mode = mode1,
                        isSelected = currentMode == mode1,
                        onClick = { viewModel.setPracticeMode(mode1) },
                        modifier = Modifier.weight(1f)
                    )
                    ModeCard(
                        mode = mode2,
                        isSelected = currentMode == mode2,
                        onClick = { viewModel.setPracticeMode(mode2) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val mode3 = PracticeMode.TENSE_FINDER
                    val mode4 = PracticeMode.IRREGULAR
                    ModeCard(
                        mode = mode3,
                        isSelected = currentMode == mode3,
                        onClick = { viewModel.setPracticeMode(mode3) },
                        modifier = Modifier.weight(1f)
                    )
                    ModeCard(
                        mode = mode4,
                        isSelected = currentMode == mode4,
                        onClick = { viewModel.setPracticeMode(mode4) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Tenses Horizontal Pill Selector
        val showTensesDisabled = currentMode != PracticeMode.SINGLE_TENSE
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = " CHOOSE YOUR TENSE",
                    color = if (showTensesDisabled) DeepNavy.copy(alpha = 0.4f) else DeepNavy,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                if (showTensesDisabled) {
                    Text(
                        text = "Tapping snaps to selection",
                        color = OchreGold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }
            Box(
                modifier = Modifier.alpha(if (showTensesDisabled) 0.5f else 1f)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(EnglishTense.entries) { tenseOption ->
                        val isSelected = tenseOption == currentTense && !showTensesDisabled
                        Box(
                            modifier = Modifier
                                .testTag("tense_pill_${tenseOption.id}")
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SleekPillBg else Color.Transparent)
                                .border(1.dp, if (isSelected) OchreGold else SlateBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setTenseFilter(tenseOption) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tenseOption.displayName,
                                color = if (isSelected) DeepNavy else CharcoalDark,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Difficulty Selector Group
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = " DIFFICULTY MOOD",
                color = DeepNavy,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("easy", "🍃 Easy", "basic positive"),
                    Triple("hard", "⚡ Hard", "negatives & irregulars"),
                    Triple("difficult", "🔥 Difficult", "mixed forms")
                ).forEach { (diffId, diffLabel, diffSub) ->
                    val isSelected = diffId == currentDifficulty
                    Card(
                        modifier = Modifier
                            .testTag("difficulty_card_$diffId")
                            .weight(1f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.setDifficultyFilter(diffId) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) SleekPillBg else LightIvory
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) OchreGold else SlateBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = diffLabel,
                                color = DeepNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = diffSub,
                                color = if (isSelected) DeepNavy.copy(alpha = 0.8f) else Color.Gray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Main Quiz Deck with customizable shake offsets during error states
        Card(
            modifier = Modifier
                .offset { IntOffset(shakeOffset.value.toInt(), 0) }
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LightIvory),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, SlateBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentQuestion?.let { q ->
                    // Animated Sparky Mascot indicating active state feedback
                    TenseBuddyMascot(
                        isAnswered = isAnswered,
                        isCorrect = selectedAnswer == q.correctValue,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    // Question Prompt configured to match the deep-purple Sleek Hero style
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(OchreGold)
                            .padding(22.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Sub-badge matching white/20 inline pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = q.tense.displayName.uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            // Format Blank highlighting with white semi-opacity overlay
                            val annotatedString = buildAnnotatedString {
                                val splitParts = q.prompt.split("___")
                                if (splitParts.size > 1) {
                                    append(splitParts[0])
                                    withStyle(
                                        style = SpanStyle(
                                            background = Color.White.copy(alpha = 0.25f),
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp
                                        )
                                    ) {
                                        append(" ______ ")
                                    }
                                    append(splitParts[1])
                                } else {
                                    append(q.prompt)
                                }
                            }
                            Text(
                                text = annotatedString,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    // Answer Options List
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        q.options.forEachIndexed { idx, option ->
                            val letter = when (idx) {
                                0 -> "A"
                                1 -> "B"
                                2 -> "C"
                                else -> "D"
                            }
                            val isSelected = selectedAnswer == option
                            val isCorrectAnswer = option == q.correctValue

                            // Color configuration based on status
                            val containerColor = when {
                                !isAnswered -> LightIvory
                                isSelected && isCorrectAnswer -> SuccessGreen.copy(alpha = 0.15f)
                                isSelected && !isCorrectAnswer -> ErrorRed.copy(alpha = 0.15f)
                                isCorrectAnswer -> SuccessGreen.copy(alpha = 0.15f)
                                else -> LightIvory.copy(alpha = 0.5f)
                            }

                            val borderColor = when {
                                !isAnswered -> if (isSelected) OchreGold else SlateBorder
                                isSelected && isCorrectAnswer -> SuccessGreen
                                isSelected && !isCorrectAnswer -> ErrorRed
                                isCorrectAnswer -> SuccessGreen
                                else -> SlateBorder
                            }

                            Card(
                                modifier = Modifier
                                    .testTag("option_button_$letter")
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable(!isAnswered) { viewModel.selectAnswer(option) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = containerColor),
                                border = BorderStroke(2.dp, borderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Circle Letter badge resembling HTML custom select buttons
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) SleekPillBg else DeepNavy.copy(alpha = 0.08f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = letter,
                                                color = DeepNavy,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Text(
                                            text = option,
                                            color = DeepNavy,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }

                                    // Dynamic feedback icons
                                    if (isAnswered) {
                                        if (isCorrectAnswer) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Correct",
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Incorrect",
                                                tint = ErrorRed,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Post-Answer Feedback Explanations Box
                    AnimatedVisibility(
                        visible = isAnswered,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            val userWasCorrect = selectedAnswer == q.correctValue
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (userWasCorrect) SuccessGreen.copy(alpha = 0.08f) else ErrorRed.copy(alpha = 0.08f))
                                    .border(
                                        1.dp,
                                        if (userWasCorrect) SuccessGreen.copy(alpha = 0.3f) else ErrorRed.copy(alpha = 0.3f),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (userWasCorrect) "🏆 EXCELLENT WORK!" else "💡 ACADEMIC INSIGHT",
                                            color = if (userWasCorrect) SuccessGreen else ErrorRed,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Text(
                                        text = q.explanation,
                                        color = DeepNavy,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 18.sp
                                    )
                                    if (q.isIrregular) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(OchreGold.copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "⚠️ Irregular Base: ${q.baseVerb.uppercase()}",
                                                color = DeepNavy,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // NEXT button styled perfectly like the active HTML button
                            Button(
                                onClick = { viewModel.generateNewQuestion() },
                                modifier = Modifier
                                    .testTag("next_question_button")
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "NEXT CHALLENGE",
                                        color = Color(0xFF381E72),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Forward",
                                        tint = Color(0xFF381E72),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OchreGold)
                    }
                }
            }
        }
    }

    // Floating Confetti Overlay rendered on top of everything
    ConfettiOverlay(
        particles = confettiParticles,
        modifier = Modifier.matchParentSize()
    )
}
}

@Composable
fun StudyGuideScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val filteredTenses = EnglishTense.entries.filter {
        it.displayName.contains(searchQuery, ignoreCase = true) ||
                it.formula.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "GRAMMAR STUDY GUIDE",
            color = DeepNavy,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 0.5.sp
        )

        // Search Filter TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().testTag("guide_search_input"),
            prefix = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).padding(end = 4.dp),
                    tint = Color.Gray
                )
            },
            placeholder = { Text("Search by tense or formula...", fontSize = 13.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OchreGold,
                unfocusedBorderColor = SlateBorder,
                focusedContainerColor = LightIvory,
                unfocusedContainerColor = LightIvory
            ),
            singleLine = true
        )

        Box(modifier = Modifier.weight(1f)) {
            if (filteredTenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No matching tenses found.",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredTenses.forEach { tense ->
                        TenseGuideCard(tense = tense)
                    }
                }
            }
        }
    }
}

@Composable
fun TenseGuideCard(tense: EnglishTense) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = LightIvory),
        border = BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tense.displayName,
                        color = DeepNavy,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DeepNavy.copy(alpha = 0.08f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tense.formula,
                            color = DeepNavy,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = DeepNavy,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Divider(color = WarmGrey)
                    
                    // Core Description
                    Text(
                        text = tense.description,
                        color = CharcoalDark,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // Standard Time Marks
                    Column {
                        Text(
                            text = "📅 TYPICAL TIME PATTERNS:",
                            color = DeepNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            tense.timeMarkers.take(4).forEach { marker ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SleekPillBg)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = marker,
                                        color = DeepNavy,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Comprehensive Patterns Grid
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "💡 SENTENCE CONJUGATION SCHEMAS:",
                            color = DeepNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                        ConjugationRow(type = "🟢 Affirmative", formula = "Subject + Action", example = getExample(tense, "aff"))
                        ConjugationRow(type = "🔴 Negative", formula = "Subject + Helper + not", example = getExample(tense, "neg"))
                        ConjugationRow(type = "🔵 Interrogative", formula = "Helper + Subject + Action?", example = getExample(tense, "int"))
                    }
                }
            }
        }
    }
}

@Composable
fun ConjugationRow(type: String, formula: String, example: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(WarmGrey.copy(alpha = 0.3f))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = type, color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(text = formula, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = example,
                color = DeepNavy,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

private fun getExample(tense: EnglishTense, type: String): String {
    return when (tense) {
        EnglishTense.PRESENT_SIMPLE -> when(type) {
            "aff" -> "He works in Seattle."
            "neg" -> "He does not work in Seattle."
            else -> "Does he work in Seattle?"
        }
        EnglishTense.PRESENT_CONTINUOUS -> when(type) {
            "aff" -> "They are practicing English."
            "neg" -> "They are not practicing English."
            else -> "Are they practicing English?"
        }
        EnglishTense.PAST_SIMPLE -> when(type) {
            "aff" -> "She visited Rome yesterday."
            "neg" -> "She did not visit Rome yesterday."
            else -> "Did she visit Rome yesterday?"
        }
        EnglishTense.PAST_CONTINUOUS -> when(type) {
            "aff" -> "We were studying geometry."
            "neg" -> "We were not studying geometry."
            else -> "Were you studying geometry?"
        }
        EnglishTense.FUTURE_SIMPLE -> when(type) {
            "aff" -> "I will solve the quiz soon."
            "neg" -> "I will not solve the quiz soon."
            else -> "Will you solve the quiz soon?"
        }
        EnglishTense.PRESENT_PERFECT -> when(type) {
            "aff" -> "Emma has finished the script."
            "neg" -> "Emma has not finished the script."
            else -> "Has Emma finished the script?"
        }
        EnglishTense.PAST_PERFECT -> when(type) {
            "aff" -> "They had cooked dinner."
            "neg" -> "They had not cooked dinner."
            else -> "Had they cooked dinner?"
        }
    }
}

@Composable
fun MasteryScreen(
    userStats: UserStatsRecord,
    tenseScores: List<TenseScore>,
    onResetStats: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "PROGRESS & ACADEMIC MASTERY",
            color = DeepNavy,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 0.5.sp
        )

        // General Stats card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WarmGrey),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("TOTAL SCORE", color = OchreGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("${userStats.totalScore} pts", color = DeepNavy, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Competency Points accumulated", color = DeepNavy.copy(alpha = 0.6f), fontSize = 10.sp)
                }
                VerticalDivider(color = SlateBorder, modifier = Modifier.height(60.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("RECORD STREAK", color = OchreGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("${userStats.highestStreak} Days", color = DeepNavy, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Highest login learning consistency", color = DeepNavy.copy(alpha = 0.6f), fontSize = 10.sp)
                }
            }
        }

        // Tense Performance bars
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightIvory),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SlateBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "📈 TENSE ACCURACY PROGRESS",
                    color = DeepNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )

                EnglishTense.entries.forEach { tense ->
                    val progressRecord = tenseScores.firstOrNull { it.tenseId == tense.id }
                    val correct = progressRecord?.correctCount ?: 0
                    val attempted = progressRecord?.attemptedCount ?: 0
                    val scoreFraction = if (attempted > 0) correct.toFloat() / attempted else 0f
                    val percentage = (scoreFraction * 100).toInt()

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = tense.displayName, color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = if (attempted > 0) "$correct/$attempted ($percentage%)" else "Unattempted",
                                color = if (attempted > 0) DeepNavy.copy(alpha = 0.8f) else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE6E1E5))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(if (scoreFraction == 0f) 0.02f else scoreFraction)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            attempted == 0 -> Color.LightGray
                                            percentage >= 80 -> SuccessGreen
                                            percentage >= 50 -> OchreGold
                                            else -> ErrorRed
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }

        // Gamified Badges Showcase
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "🏆 ACQUIRED BADGES",
                color = DeepNavy,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )

            // Evaluate badge unlocking status
            val badges = listOf(
                BadgeItem("Tense Novice", "🥉", "Attempted practicing your first sentence.", userStats.totalScore >= 1),
                BadgeItem("Streak Starter", "🔥", "Maintained a 2-Day target streak.", userStats.streakCount >= 2),
                BadgeItem("Academic Scholar", "📖", "Amassed 10 correct tenses answers.", userStats.totalScore >= 10),
                BadgeItem("Grammar Knight", "⚔️", "Amassed 30 Competency Points.", userStats.totalScore >= 30),
                BadgeItem("Conjugation Master", "👑", "Achieved score streak of 5+ days.", userStats.highestStreak >= 5)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                badges.take(3).forEach { badge ->
                    BadgeCard(badge = badge, modifier = Modifier.weight(1f))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                badges.drop(3).forEach { badge ->
                    BadgeCard(badge = badge, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.weight(1f)) // spacer to align 2 items
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reset statistics safe button
        Button(
            onClick = onResetStats,
            modifier = Modifier
                .testTag("reset_stats_button")
                .fillMaxWidth()
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(16.dp)
                )
                Text("RESET ALL PROGRESS", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

data class BadgeItem(val title: String, val icon: String, val desc: String, val unlocked: Boolean)

@Composable
fun BadgeCard(badge: BadgeItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.unlocked) LightIvory else WarmGrey.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (badge.unlocked) OchreGold.copy(alpha = 0.5f) else Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (badge.unlocked) OchreGold.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badge.unlocked) badge.icon else "🔒",
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = badge.title,
                color = if (badge.unlocked) DeepNavy else Color.Gray,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = badge.desc,
                color = Color.Gray,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                lineHeight = 10.sp,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavBarItem(
            label = "Practice",
            icon = Icons.Default.Star,
            isSelected = activeTab == "quiz",
            modifier = Modifier.testTag("nav_item_practice"),
            onClick = { onTabSelected("quiz") }
        )
        NavBarItem(
            label = "Study Guide",
            icon = Icons.Default.List,
            isSelected = activeTab == "guide",
            modifier = Modifier.testTag("nav_item_guide"),
            onClick = { onTabSelected("guide") }
        )
        NavBarItem(
            label = "Mastery",
            icon = Icons.Default.Person,
            isSelected = activeTab == "mastery",
            modifier = Modifier.testTag("nav_item_mastery"),
            onClick = { onTabSelected("mastery") }
        )
    }
}

@Composable
fun NavBarItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEADDFF))
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF21005D),
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF1C1B1F).copy(alpha = 0.6f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) Color(0xFF1C1B1F) else Color(0xFF1C1B1F).copy(alpha = 0.6f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ModeCard(
    mode: PracticeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SleekPillBg else LightIvory
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) OchreGold else SlateBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) OchreGold.copy(alpha = 0.2f) else WarmGrey),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mode.icon, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = mode.displayName,
                    color = DeepNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = when (mode) {
                        PracticeMode.SINGLE_TENSE -> "Master one tense"
                        PracticeMode.MIXED -> "All tenses blended"
                        PracticeMode.TENSE_FINDER -> "Identify the tense"
                        PracticeMode.IRREGULAR -> "Conjugate irregulars"
                    },
                    color = CharcoalDark.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
