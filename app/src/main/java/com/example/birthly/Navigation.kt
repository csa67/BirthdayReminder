package com.example.birthly

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.birthly.views.AddEventScreen
import com.example.birthly.views.GreetingScreen
import com.example.birthly.views.HomeScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(viewModel: UserViewModel){
    val navController = rememberNavController()
    val userState = viewModel.userState.value

    NavHost(navController,
        startDestination = if(userState != null) "home" else "signIn"){
        composable("home"){
            HomeScreen(navController)
        }
        composable("signIn") {
            SignInScreen(
                onSignIn = { email, password -> viewModel.signIn(email, password) },
                onSignUp = { email, password -> viewModel.signUp(email, password = password) }
            )
        }
        composable("addNewBirthday"){
            AddEventScreen(viewModel, navController)
        }
        
        composable("composeGreeting"){
            GreetingScreen(viewModel)
        }
    }
}