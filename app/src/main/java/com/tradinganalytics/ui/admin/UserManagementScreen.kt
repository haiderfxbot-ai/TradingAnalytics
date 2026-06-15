package com.tradinganalytics.ui.admin


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tradinganalytics.core.constants.AppConstants
import com.tradinganalytics.core.theme.DarkThemeColors
import com.tradinganalytics.core.theme.GradientDefs

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(AdminEvent.ClearSnackbar)
        }
    }

    val isDark = MaterialTheme.colorScheme.background == DarkThemeColors.PremiumBackground
    val bgGradient = if (isDark) GradientDefs.backgroundDark else GradientDefs.backgroundLight

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "User Management",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AdminEvent.OpenCreateDialog) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add User",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(AdminEvent.OnSearchQueryChange(it)) },
                    placeholder = { Text("Search users...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    val filteredUsers = if (state.searchQuery.isBlank()) {
                        state.users
                    } else {
                        state.users.filter {
                            it.username.contains(state.searchQuery, ignoreCase = true) ||
                                it.displayName.contains(state.searchQuery, ignoreCase = true)
                        }
                    }

                    if (filteredUsers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (state.searchQuery.isNotBlank()) "No users found" else "No users yet",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredUsers, key = { it.id }) { user ->
                                UserCard(
                                    user = user,
                                    onEdit = { viewModel.onEvent(AdminEvent.OpenEditDialog(user)) },
                                    onDelete = { viewModel.onEvent(AdminEvent.ShowDeleteConfirm(user)) },
                                    onToggleStatus = {
                                        if (user.status == AppConstants.AccountStatus.ACTIVE) {
                                            viewModel.onEvent(AdminEvent.DisableUser(user.id))
                                        } else {
                                            viewModel.onEvent(AdminEvent.EnableUser(user.id))
                                        }
                                    },
                                    onLock = {
                                        if (user.status == AppConstants.AccountStatus.LOCKED) {
                                            viewModel.onEvent(AdminEvent.UnlockUser(user.id))
                                        } else {
                                            viewModel.onEvent(AdminEvent.LockUser(user.id))
                                        }
                                    },
                                    onResetPassword = {
                                        viewModel.onEvent(AdminEvent.ShowResetPasswordDialog(user))
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }

            if (state.showUserDialog) {
                UserFormDialog(
                    formState = state.userFormState,
                    isEdit = state.editingUser != null,
                    onDismiss = { viewModel.onEvent(AdminEvent.DismissDialog) },
                    onSubmit = { viewModel.onEvent(AdminEvent.SubmitUserForm) },
                    onUsernameChange = { viewModel.onEvent(AdminEvent.OnFormUsernameChange(it)) },
                    onPasswordChange = { viewModel.onEvent(AdminEvent.OnFormPasswordChange(it)) },
                    onDisplayNameChange = { viewModel.onEvent(AdminEvent.OnFormDisplayNameChange(it)) },
                    onRoleChange = { viewModel.onEvent(AdminEvent.OnFormRoleChange(it)) }
                )
            }

            state.showDeleteConfirm?.let { user ->
                DeleteConfirmDialog(
                    username = user.username,
                    onConfirm = {
                        viewModel.onEvent(AdminEvent.DeleteUser(user.id))
                    },
                    onDismiss = { viewModel.onEvent(AdminEvent.DismissDeleteConfirm) }
                )
            }

            state.showResetPasswordDialog?.let { user ->
                ResetPasswordDialog(
                    username = user.username,
                    passwordValue = state.resetPasswordValue,
                    onPasswordChange = { viewModel.onEvent(AdminEvent.OnResetPasswordChange(it)) },
                    onConfirm = { viewModel.onEvent(AdminEvent.SubmitResetPassword) },
                    onDismiss = { viewModel.onEvent(AdminEvent.DismissResetPasswordDialog) }
                )
            }

            if (state.showLoginHistory) {
                LoginHistoryDialog(
                    username = state.loginHistoryUsername,
                    history = state.loginHistory,
                    onDismiss = { viewModel.onEvent(AdminEvent.DismissLoginHistory) }
                )
            }
        }
    }
}

@Composable
private fun UserCard(
    user: UserListItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStatus: () -> Unit,
    onLock: () -> Unit,
    onResetPassword: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkThemeColors.CardDark.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    GradientDefs.glassPanel,
                    RoundedCornerShape(14.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val roleColor = if (user.role == AppConstants.UserRoles.ADMIN) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                        Text(
                            text = user.role,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier
                                .background(roleColor.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                        val statusColor = when (user.status) {
                            AppConstants.AccountStatus.ACTIVE -> Color(0xFF00E676)
                            AppConstants.AccountStatus.DISABLED -> Color(0xFFFFAB40)
                            AppConstants.AccountStatus.LOCKED -> Color(0xFFFF5252)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        val statusLabel = when (user.status) {
                            AppConstants.AccountStatus.ACTIVE -> "Active"
                            AppConstants.AccountStatus.DISABLED -> "Disabled"
                            AppConstants.AccountStatus.LOCKED -> "Locked"
                            else -> user.status
                        }
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Actions",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { showMenu = false; onEdit() },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (user.status == AppConstants.AccountStatus.ACTIVE) "Disable"
                                    else "Enable"
                                )
                            },
                            onClick = { showMenu = false; onToggleStatus() },
                            leadingIcon = {
                                Icon(
                                    if (user.status == AppConstants.AccountStatus.ACTIVE) Icons.Default.Block
                                    else Icons.Default.CheckCircle,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (user.status == AppConstants.AccountStatus.LOCKED) "Unlock"
                                    else "Lock"
                                )
                            },
                            onClick = { showMenu = false; onLock() },
                            leadingIcon = {
                                Icon(
                                    if (user.status == AppConstants.AccountStatus.LOCKED) Icons.Default.LockOpen
                                    else Icons.Default.Lock,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reset Password") },
                            onClick = { showMenu = false; onResetPassword() },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = { showMenu = false; onDelete() },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserFormDialog(
    formState: UserFormState,
    isEdit: Boolean,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onRoleChange: (String) -> Unit
) {
    var roleExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "Edit User" else "Create User",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = formState.username,
                    onValueChange = onUsernameChange,
                    label = { Text("Username") },
                    isError = formState.usernameError != null,
                    supportingText = formState.usernameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (!isEdit) {
                    OutlinedTextField(
                        value = formState.password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        isError = formState.passwordError != null,
                        supportingText = formState.passwordError?.let { { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = formState.displayName,
                    onValueChange = onDisplayNameChange,
                    label = { Text("Display Name") },
                    isError = formState.displayNameError != null,
                    supportingText = formState.displayNameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box {
                    OutlinedTextField(
                        value = formState.role,
                        onValueChange = {},
                        label = { Text("Role") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { roleExpanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Select role")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { roleExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    DropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        AppConstants.UserRoles.all.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    onRoleChange(role)
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isEdit) "Update" else "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteConfirmDialog(
    username: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete User", fontWeight = FontWeight.Bold)
        },
        text = {
            Text("Are you sure you want to delete user \"$username\"? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ResetPasswordDialog(
    username: String,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Reset Password", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Set new password for \"$username\"",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = passwordValue,
                    onValueChange = onPasswordChange,
                    label = { Text("New Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun LoginHistoryDialog(
    username: String,
    history: List<LoginHistoryDisplay>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Login History - $username", fontWeight = FontWeight.Bold)
        },
        text = {
            if (history.isEmpty()) {
                Text(
                    text = "No login history found",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column {
                    history.take(20).forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = SimpleDateFormat("MMM dd HH:mm", Locale.US)
                                    .format(Date(entry.loginTime)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = entry.deviceInfo.take(20),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
