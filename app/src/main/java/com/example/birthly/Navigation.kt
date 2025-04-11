package com.example.birthly

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.birthly.viewmodel.BirthlyViewModel
import com.example.birthly.views.AddEventScreen
import com.example.birthly.views.AllBirthdaysScreen
import com.example.birthly.views.GreetingScreen
import com.example.birthly.views.HomeScreen

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("Home", "home", Icons.Default.Home),
    TopLevelRoute("Add Birthday", "addNewBirthday", Icons.Default.Cake),
    TopLevelRoute("People", "allBirthdays", Icons.Default.People)
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(viewModel: BirthlyViewModel){
    val navController = rememberNavController()
    val userState = viewModel.userState.value

    Scaffold(
        bottomBar = {
            NavigationBar{
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                topLevelRoutes.forEach{ topLevelRoute ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true,
                        onClick = { navController.navigate(topLevelRoute.route){
                            popUpTo(navController.graph.findStartDestination().id){
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                                  },
                        label = { Text(topLevelRoute.name)},
                        icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = if (userState != null) "home" else "signIn",
            Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController, viewModel)
            }
            composable("signIn") {
                SignInScreen(
                    onSignIn = { email, password -> viewModel.signIn(email, password) },
                    onSignUp = { email, password -> viewModel.signUp(email, password = password) }
                )
            }
            composable("addNewBirthday") {
                AddEventScreen(viewModel, navController)
            }

            composable("composeGreeting") {
                GreetingScreen(viewModel)
            }

            composable("allBirthdays") {
                AllBirthdaysScreen(viewModel)
            }
        }
    }
}