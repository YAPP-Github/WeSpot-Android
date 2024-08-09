package com.bff.wespot

import androidx.navigation.NavController
import com.bff.wespot.entire.screen.screen.EntireNavigator
import com.bff.wespot.entire.screen.screen.destinations.AccountSettingScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.BlockListScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.CharacterEditScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.NotificationSettingScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.ProfileEditScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.RevokeConfirmScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.RevokeScreenDestination
import com.bff.wespot.entire.screen.screen.destinations.SettingScreenDestination
import com.bff.wespot.entire.screen.screen.edit.CharacterEditNavigator
import com.bff.wespot.entire.screen.screen.edit.ProfileEditNavArgs
import com.bff.wespot.entire.screen.screen.edit.ProfileEditNavigator
import com.bff.wespot.entire.screen.screen.setting.AccountSettingNavigator
import com.bff.wespot.entire.screen.screen.setting.BlockListNavigator
import com.bff.wespot.entire.screen.screen.setting.NotificationSettingNavigator
import com.bff.wespot.entire.screen.screen.setting.RevokeConfirmNavigator
import com.bff.wespot.entire.screen.screen.setting.RevokeNavigator
import com.bff.wespot.entire.screen.screen.setting.SettingNavigator
import com.bff.wespot.message.screen.MessageNavigator
import com.bff.wespot.message.screen.MessageScreenArgs
import com.bff.wespot.message.screen.ReservedMessageNavigator
import com.bff.wespot.message.screen.ReservedMessageScreenArgs
import com.bff.wespot.message.screen.destinations.MessageEditScreenDestination
import com.bff.wespot.message.screen.destinations.MessageScreenDestination
import com.bff.wespot.message.screen.destinations.MessageWriteScreenDestination
import com.bff.wespot.message.screen.destinations.ReceiverSelectionScreenDestination
import com.bff.wespot.message.screen.destinations.ReservedMessageScreenDestination
import com.bff.wespot.message.screen.send.EditMessageScreenArgs
import com.bff.wespot.message.screen.send.MessageEditNavigator
import com.bff.wespot.message.screen.send.MessageWriteNavigator
import com.bff.wespot.message.screen.send.MessageWriteScreenArgs
import com.bff.wespot.message.screen.send.ReceiverSelectionNavigator
import com.bff.wespot.message.screen.send.ReceiverSelectionScreenArgs
import com.bff.wespot.notification.screen.NotificationNavigator
import com.bff.wespot.notification.screen.destinations.NotificationScreenDestination
import com.bff.wespot.vote.screen.CharacterSettingNavigator
import com.bff.wespot.vote.screen.IndividualVoteArgs
import com.bff.wespot.vote.screen.IndividualVoteNavigator
import com.bff.wespot.vote.screen.IntroductionArgs
import com.bff.wespot.vote.screen.IntroductionNavigator
import com.bff.wespot.vote.screen.VoteNavigator
import com.bff.wespot.vote.screen.VoteResultNavigator
import com.bff.wespot.vote.screen.VoteResultScreenArgs
import com.bff.wespot.vote.screen.VoteStorageNavigator
import com.bff.wespot.vote.screen.VotingNavigator
import com.bff.wespot.vote.screen.destinations.CharacterSettingScreenDestination
import com.bff.wespot.vote.screen.destinations.IndividualVoteScreenDestination
import com.bff.wespot.vote.screen.destinations.IntroductionScreenDestination
import com.bff.wespot.vote.screen.destinations.VoteResultScreenDestination
import com.bff.wespot.vote.screen.destinations.VoteStorageScreenDestination
import com.bff.wespot.vote.screen.destinations.VotingScreenDestination
import com.ramcosta.composedestinations.dynamic.within
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.NavGraphSpec

class CommonNavGraphNavigator(
    private val navGraph: NavGraphSpec,
    private val navController: NavController,
) : VoteNavigator,
    MessageNavigator,
    ReceiverSelectionNavigator,
    MessageWriteNavigator,
    MessageEditNavigator,
    EntireNavigator,
    SettingNavigator,
    NotificationSettingNavigator,
    AccountSettingNavigator,
    RevokeNavigator,
    RevokeConfirmNavigator,
    BlockListNavigator,
    VotingNavigator,
    VoteResultNavigator,
    VoteStorageNavigator,
    ReservedMessageNavigator,
    IndividualVoteNavigator,
    ProfileEditNavigator,
    CharacterEditNavigator,
    NotificationNavigator,
    CharacterSettingNavigator,
    IntroductionNavigator {
    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun navigateToVotingScreen() {
        navController.navigate(VotingScreenDestination within navGraph)
    }

    override fun navigateReceiverSelectionScreen(args: ReceiverSelectionScreenArgs) {
        navController.navigate(ReceiverSelectionScreenDestination(args) within navGraph)
    }

    override fun navigateMessageWriteScreen(args: MessageWriteScreenArgs) {
        navController.navigate(MessageWriteScreenDestination(args) within navGraph)
    }

    override fun navigateMessageScreen(args: MessageScreenArgs) {
        navController.navigate(MessageScreenDestination(args) within navGraph)
    }

    override fun navigateMessageEditScreen(args: EditMessageScreenArgs) {
        navController.navigate(MessageEditScreenDestination(args) within navGraph)
    }

    override fun navigateToReservedMessageScreen(args: ReservedMessageScreenArgs) {
        navController.navigate(ReservedMessageScreenDestination(args) within navGraph)
    }

    override fun navigateNotificationScreen() {
        navController.navigate(NotificationScreenDestination within navGraph)
    }

    override fun navigateToResultScreen(args: VoteResultScreenArgs) {
        navController.navigate(VoteResultScreenDestination(args) within navGraph)
    }

    override fun navigateToVoteHome() {
        navController.popBackStack(navGraph.startRoute.route, inclusive = false, saveState = true)
    }

    override fun navigateToVoteResultScreen(args: VoteResultScreenArgs) {
        navController.navigate(VoteResultScreenDestination(args) within navGraph)
    }

    override fun navigateToSetting() {
        navController.navigate(SettingScreenDestination within navGraph)
    }

    override fun navigateToNotificationSetting() {
        navController.navigate(NotificationSettingScreenDestination within navGraph)
    }

    override fun navigateToAccountSetting() {
        navController.navigate(AccountSettingScreenDestination within navGraph)
    }

    override fun navigateToRevokeScreen() {
        navController.navigate(RevokeScreenDestination within navGraph)
    }

    override fun navigateToRevokeConfirmScreen() {
        navController.navigate(RevokeConfirmScreenDestination within navGraph)
    }

    override fun navigateToVoteStorageScreen() {
        navController.navigate(VoteStorageScreenDestination within navGraph)
    }

    override fun navigateToIndividualVote(args: IndividualVoteArgs) {
        navController.navigate(IndividualVoteScreenDestination(args) within navGraph)
    }

    override fun navigateToBlockListScreen() {
        navController.navigate(BlockListScreenDestination within navGraph)
    }

    override fun navigateToCharacterScreen() {
        navController.navigate(CharacterSettingScreenDestination within navGraph)
    }

    override fun navigateToIntroduction(args: IntroductionArgs) {
        navController.navigate(IntroductionScreenDestination(args) within navGraph)
    }

    override fun navigateToCharacterEditScreen() {
        navController.navigate(CharacterEditScreenDestination within navGraph)
    }

    override fun navigateToProfileEditScreen(args: ProfileEditNavArgs) {
        navController.navigate(ProfileEditScreenDestination(args) within navGraph)
    }
}
