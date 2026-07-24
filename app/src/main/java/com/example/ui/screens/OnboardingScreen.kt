package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MrbBackground
import com.example.ui.theme.MrbGold

import androidx.compose.material3.Surface
import com.example.ui.theme.MrbGoldOutline

data class OnboardingPageData(
    val titlePart1: String,
    val whitePart: String,
    val goldPart: String,
    val titlePart2: String,
    val highlights: List<String>,
    val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPageData(
        titlePart1 = "Satu Platform,\n",
        whitePart = "Ribuan ",
        goldPart = "Pilihan Mobil",
        titlePart2 = "\nKalimantan",
        highlights = listOf(
            "Stok Mobil Terlengkap",
            "Harga Transparan",
            "Aman & Terpercaya"
        ),
        imageRes = R.drawable.mrb_suv_bg
    ),
    OnboardingPageData(
        titlePart1 = "Jaminan ",
        whitePart = "Harga ",
        goldPart = "Transparan",
        titlePart2 = "\n& Garansi Resmi",
        highlights = listOf(
            "Inspeksi Kualitas 160 Titik",
            "Bebas Bekas Laka & Banjir",
            "Garansi Mesin 1 Tahun"
        ),
        imageRes = R.drawable.img_bg_veloz_1784874232737
    ),
    OnboardingPageData(
        titlePart1 = "Kemudahan ",
        whitePart = "Proses ",
        goldPart = "Kredit Fast",
        titlePart2 = "\n& Booking Unit",
        highlights = listOf(
            "DP Ringan & Bunga Rendah",
            "Proses Cepat 1x24 Jam",
            "Antar Unit Kalteng & Kalsel"
        ),
        imageRes = R.drawable.img_bg_pajero_1784874257799
    )
)

val GoldThemeColor = MrbGold
val DarkBgColor = MrbBackground

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBgColor)
    ) {
        // Fullscreen Background SUV Image
        val currentData = onboardingPages[pagerState.currentPage]
        Image(
            painter = painterResource(id = currentData.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Gradient Overlay to isolate top text & bottom controls
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkBgColor.copy(alpha = 0.98f),
                            DarkBgColor.copy(alpha = 0.90f),
                            DarkBgColor.copy(alpha = 0.40f),
                            DarkBgColor.copy(alpha = 0.85f),
                            DarkBgColor.copy(alpha = 0.98f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val data = onboardingPages[page]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // MRB Top Logo Badge
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(600))
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MrbGold,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MrbGoldOutline),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(76.dp)
                            .padding(bottom = 4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_mrb_logo_badge_1784874215022),
                            contentDescription = "MRB Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Text
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(700, delayMillis = 150)) + slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(700, delayMillis = 150)
                    )
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            ) {
                                append(data.titlePart1)
                                append(data.whitePart)
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = GoldThemeColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp
                                )
                            ) {
                                append(data.goldPart)
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            ) {
                                append(data.titlePart2)
                            }
                        },
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Checklist Items
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(800, delayMillis = 300))
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        data.highlights.forEach { highlightText ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                // Gold Circle with Black Checkmark
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(GoldThemeColor, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = highlightText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Fixed Controls Container
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.96f else 1.0f,
                animationSpec = tween(150),
                label = "buttonScale"
            )

            // Gold Capsule Button (#FFD24A -> #D99A00)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .scale(buttonScale)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFD24A),
                                Color(0xFFF5B72D),
                                Color(0xFFD99A00)
                            )
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onNavigateToLogin
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Mulai Sekarang",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D0D0D)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF0D0D0D),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingPages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) GoldThemeColor else Color(0xFF555555))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Version Label
            Text(
                text = "Versi 1.0.0",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}



