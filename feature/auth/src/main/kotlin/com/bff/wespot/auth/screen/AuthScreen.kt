package com.bff.wespot.auth.screen

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bff.wespot.auth.R
import com.bff.wespot.auth.state.AuthAction
import com.bff.wespot.auth.viewmodel.AuthViewModel
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.ui.component.DotIndicators
import com.bff.wespot.ui.component.WSCarousel
import com.bff.wespot.ui.util.KakaoLoginManager
import com.bff.wespot.ui.util.handleSideEffect
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Destination
@RootNavGraph(start = true)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
) {
    val pagerState = rememberPagerState { 3 }
    val kakaoLogin = KakaoLoginManager()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    handleSideEffect(viewModel.sideEffect)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
    ) {
        WSCarousel(
            pagerState = pagerState,
        ) {
            val composition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(
                    AuthCarouselItem.entries[it].resId,
                ),
            )
            Column {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = AuthCarouselItem.entries[it].background),
                        contentDescription = stringResource(R.string.onboarding_background),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    )

                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    )
                }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = AuthCarouselItem.entries[it].title),
                        style = StaticTypeScale.Default.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            DotIndicators(pagerState = pagerState, modifier = Modifier.padding(top = 12.dp))
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Button(
            onClick = {
                coroutineScope.launch {
                    runCatching {
                        val token = kakaoLogin.loginWithKakao(context)
                        viewModel.onAction(AuthAction.LoginWithKakao(token))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 60.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFFEE500)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFEE500),
            ),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.kakao),
                contentDescription = stringResource(
                    id = R.string.kakao_icon,
                ),
                tint = Color.Black,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(id = R.string.continue_with_kakao),
                style = StaticTypeScale.Default.body4.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
            )
        }
    }
}

private enum class AuthCarouselItem(
    @RawRes val resId: Int,
    @DrawableRes val background: Int,
    @StringRes val title: Int,
) {
    FIRST(R.raw.onboarding01, R.drawable.onboarding_01_bkg, R.string.onboarding1),
    SignUp(R.raw.onboarding02, R.drawable.onboarding_02_bkg, R.string.onboarding2),
    Welcome(R.raw.onboarding03, R.drawable.onboarding_03_bkg, R.string.onboarding3),
}
