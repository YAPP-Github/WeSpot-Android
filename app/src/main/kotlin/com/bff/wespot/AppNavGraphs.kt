package com.bff.wespot

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import com.bff.wespot.entire.screen.screen.destinations.AccountSettingScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.BlockListScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.EntireScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.NotificationSettingScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.RevokeConfirmScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.RevokeScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.SettingScreenDestination
import com.bff.wespot.message.screen.destinations.MessageEditScreenDestination
import com.bff.wespot.message.screen.destinations.MessageScreenDestination
import com.bff.wespot.message.screen.destinations.MessageWriteScreenDestination
import com.bff.wespot.message.screen.destinations.ReceiverSelectionScreenDestination
import com.bff.wespot.message.screen.destinations.ReservedMessageScreenDestination
import com.bff.wespot.message.viewmodel.SendViewModel
import com.bff.wespot.notification.screen.destinations.NotificationScreenDestination
import com.bff.wespot.navigation.Navigator
import com.bff.wespot.vote.screen.destinations.CharacterSettingScreenDestination
import com.bff.wespot.vote.screen.destinations.IndividualVoteScreenDestination
import com.bff.wespot.vote.screen.destinations.IntroductionScreenDestination
import com.bff.wespot.vote.screen.destinations.VoteHomeScreenDestination
import com.bff.wespot.vote.screen.destinations.VoteResultScreenDestination
import com.bff.wespot.vote.screen.destinations.VoteStorageScreenDestination
import com.bff.wespot.vote.screen.destinations.VotingScreenDestination
import com.bff.wespot.vote.viewmodel.VotingViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.dynamic.routedIn
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec

object AppNavGraphs {
    val vote = object : NavGraphSpec {
        override val route = "vote"

        override val startRoute = VoteHomeScreenDestination routedIn this

        override val destinationsByRoute = listOf<DestinationSpec<*>>(
            VoteHomeScreenDestination,
            VotingScreenDestination,
            VoteResultScreenDestination,
            VoteStorageScreenDestination,
            IndividualVoteScreenDestination,
            CharacterSettingScreenDestination,
            IntroductionScreenDestination,
        ).routedIn(this)
            .associateBy { it.route }
    }

    val message = object : NavGraphSpec {
        override val route = "message"

        override val startRoute = MessageScreenDestination routedIn this

        override val destinationsByRoute = listOf<DestinationSpec<*>>(
            MessageScreenDestination,
            MessageWriteScreenDestination,
            MessageEditScreenDestination,
            ReceiverSelectionScreenDestination,
            ReservedMessageScreenDestination,
        ).routedIn(this)
            .associateBy { it.route }
    }

    val entire = object : NavGraphSpec {
        override val route = "entire"

        override val startRoute = EntireScreenDestination routedIn this

        override val destinationsByRoute = listOf<DestinationSpec<*>>(
            EntireScreenDestination,
            SettingScreenDestination,
            NotificationSettingScreenDestination,
            AccountSettingScreenDestination,
            RevokeScreenDestination,
            RevokeConfirmScreenDestination,
            BlockListScreenDestination,
        ).routedIn(this)
            .associateBy { it.route }
    }

    val notification = object : NavGraphSpec {
        override val route = "notification"

        override val startRoute = NotificationScreenDestination routedIn this

        override val destinationsByRoute = listOf<DestinationSpec<*>>(
            NotificationScreenDestination,
        ).routedIn(this)
            .associateBy { it.route }
    }

    val root = object : NavGraphSpec {
        override val route = "root"

        override val startRoute = vote

        override val destinationsByRoute = emptyMap<String, DestinationSpec<*>>()

        override val nestedNavGraphs = listOf(
            vote,
            message,
            entire,
            notification,
        )
    }
}

private val tabScreenNames = listOf(
    "vote/vote_home_screen",
    "message/message_screen?isMessageSent={isMessageSent}",
)

fun NavDestination.navGraph(): NavGraphSpec {
    hierarchy.forEach { destination ->
        AppNavGraphs.root.nestedNavGraphs.forEach { navGraph ->
            if (destination.route == navGraph.route) {
                return navGraph
            }
        }
    }

    throw ClassNotFoundException("Unknown nav graph for destination $route")
}

fun NavDestination.checkDestination(): Boolean {
    hierarchy.forEach { destination ->
        tabScreenNames.forEach { name ->
            if (destination.route == name) {
                return true
            }
        }
    }
    return false
}

fun DestinationScopeWithNoDependencies<*>.currentNavigator(): CommonNavGraphNavigator {
    return CommonNavGraphNavigator(
        navBackStackEntry.destination.navGraph(),
        navController,
    )
}

@Composable
internal fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigator: Navigator,
) {
    val engine = rememberNavHostEngine(
        rootDefaultAnimations = RootNavGraphDefaultAnimations(
            enterTransition = { defaultEnterTransition(initialState, targetState) },
            exitTransition = { defaultExitTransition(initialState, targetState) },
        ),
    )

    val sendViewModel: SendViewModel = hiltViewModel()
    val votingViewModel: VotingViewModel = hiltViewModel()

    DestinationsNavHost(
        navGraph = AppNavGraphs.root,
        navController = navController,
        engine = engine,
        modifier = modifier,
        dependenciesContainerBuilder = {
            dependency(currentNavigator())
            dependency(navigator)
            dependency(sendViewModel)
            dependency(votingViewModel)
            dependency(navigator)
        },
    )
}

private fun AnimatedContentTransitionScope<*>.defaultEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): EnterTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeIn()
    }
    return fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start)
}

private fun AnimatedContentTransitionScope<*>.defaultExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): ExitTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeOut()
    }
    return fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End)
}

private val NavDestination.hostNavGraph: NavGraph
    get() = hierarchy.first { it is NavGraph } as NavGraph
