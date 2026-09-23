package com.example.ledger.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import com.example.ledger.presentation.dashboard.DashboardScreen
import com.example.ledger.presentation.categories.CategoriesScreen
import com.example.ledger.presentation.onboarding.OnboardingScreen
import com.example.ledger.presentation.overdraft.FulizaScreen
import com.example.ledger.presentation.settings.SettingsScreen
import com.example.ledger.presentation.smart_rules.SmartRulesScreen
import com.example.ledger.presentation.transactions.TransactionListScreen
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText

private data class BottomTab(val destination: LedgerDestination, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomTabs = listOf(
    BottomTab(LedgerDestination.Dashboard, "Home", Icons.Filled.Home),
    BottomTab(LedgerDestination.Transactions, "Transactions", Icons.Filled.List),
    BottomTab(LedgerDestination.Fuliza, "Fuliza", Icons.Filled.AccountBalanceWallet),
    BottomTab(LedgerDestination.SmartRules, "Rules", Icons.Filled.Rule),
    BottomTab(LedgerDestination.Settings, "Settings", Icons.Filled.Settings),
)

@Composable
fun LedgerNavGraph(startAtOnboarding: Boolean) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination

    val showBottomBar = bottomTabs.any { currentRoute?.hierarchy?.any { d -> d.route == it.destination.route } == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                androidx.compose.material3.Divider(color = WiseColors.Divider, thickness = 0.5.dp)
                NavigationBar(containerColor = WiseColors.Canvas, tonalElevation = 0.dp) {
                    bottomTabs.forEach { tab ->
                        val selected = currentRoute?.hierarchy?.any { it.route == tab.destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label, style = WiseText.Tab) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = WiseColors.Forest,
                                selectedTextColor = WiseColors.Forest,
                                unselectedIconColor = WiseColors.TextSecondary,
                                unselectedTextColor = WiseColors.TextSecondary,
                                indicatorColor = WiseColors.BrightTint,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (startAtOnboarding) LedgerDestination.Onboarding.route else LedgerDestination.Dashboard.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(LedgerDestination.Onboarding.route) {
                OnboardingScreen(onFinished = {
                    navController.navigate(LedgerDestination.Dashboard.route) {
                        popUpTo(LedgerDestination.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(LedgerDestination.Dashboard.route) { DashboardScreen() }
            composable(LedgerDestination.Transactions.route) { TransactionListScreen() }
            composable(LedgerDestination.Categories.route) { CategoriesScreen() }
            composable(LedgerDestination.SmartRules.route) { SmartRulesScreen() }
            composable(LedgerDestination.Fuliza.route) { FulizaScreen() }
            composable(LedgerDestination.Settings.route) { SettingsScreen() }
        }
    }
}
