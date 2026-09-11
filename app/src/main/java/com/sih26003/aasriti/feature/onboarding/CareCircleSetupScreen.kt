package com.sih26003.aasriti.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih26003.aasriti.core.ui.components.AasritiLogoBadge
import com.sih26003.aasriti.core.ui.theme.AasritiColorTokens
import com.sih26003.aasriti.data.local.entities.PatientEntity
import com.sih26003.aasriti.data.local.entities.RelationshipEntity
import com.sih26003.aasriti.data.repository.PatientRepository
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun CareCircleSetupScreen(
    patientRepository: PatientRepository,
    patientId: String?,
    onNavigateBack: () -> Unit,
    onComplete: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedPatient by remember { mutableStateOf<PatientEntity?>(null) }
    val relationships = remember { mutableStateListOf<RelationshipEntity>() }

    var nameInput by remember { mutableStateOf("") }
    var selectedRelation by remember { mutableStateOf("SON") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val relationTypes = listOf("SON", "DAUGHTER", "SPOUSE", "GRANDSON", "GRANDDAUGHTER", "FRIEND")

    LaunchedEffect(patientId) {
        val targetId = patientId ?: "PATIENT_AITA_BORAH"
        val patient = patientRepository.getPatientById(targetId)
            ?: patientRepository.getPatientById("PATIENT_AITA_BORAH")
        selectedPatient = patient

        val existing = patientRepository.getRelationshipsList(targetId)
        relationships.clear()
        relationships.addAll(existing)

        if (relationships.isEmpty()) {
            relationships.add(
                RelationshipEntity(
                    id = UUID.randomUUID().toString(),
                    patientId = targetId,
                    name = "Ramen Borah",
                    relationshipType = "SON"
                )
            )
            relationships.add(
                RelationshipEntity(
                    id = UUID.randomUUID().toString(),
                    patientId = targetId,
                    name = "Meera Borah",
                    relationshipType = "DAUGHTER"
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AasritiColorTokens.WarmIvory)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onNavigateBack,
                colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.SoftCream),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
            ) {
                Text(
                    text = "← Back",
                    color = AasritiColorTokens.DeepCharcoal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            AasritiLogoBadge(size = 46.dp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AasritiColorTokens.SoftCream),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = AasritiColorTokens.DeepCranberryEmergency,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Emergency & Memory Circle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add family members and primary responders for ${selectedPatient?.pseudonymCode?.substringBefore(" •") ?: "the elder"}. Their names and voices power Family Trivia, Reminders, and the Emergency SOS button.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AasritiColorTokens.WarmSlate,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Add Family Member / Responder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal
                )
            }

            item {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Contact Full Name") },
                    placeholder = { Text("e.g. Ramesh Baruah") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AasritiColorTokens.DeepNortheastForest,
                        unfocusedBorderColor = AasritiColorTokens.WarmStoneBorder,
                        focusedContainerColor = AasritiColorTokens.SoftCream,
                        unfocusedContainerColor = AasritiColorTokens.SoftCream
                    ),
                    singleLine = true
                )
            }

            item {
                Text(
                    text = "Relationship to Elder",
                    style = MaterialTheme.typography.labelLarge,
                    color = AasritiColorTokens.WarmSlate
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    relationTypes.take(3).forEach { rel ->
                        FilterChip(
                            selected = selectedRelation == rel,
                            onClick = { selectedRelation = rel },
                            label = { Text(rel.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AasritiColorTokens.DeepNortheastForest,
                                selectedLabelColor = AasritiColorTokens.WarmIvory
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    relationTypes.drop(3).forEach { rel ->
                        FilterChip(
                            selected = selectedRelation == rel,
                            onClick = { selectedRelation = rel },
                            label = { Text(rel.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AasritiColorTokens.DeepNortheastForest,
                                selectedLabelColor = AasritiColorTokens.WarmIvory
                            )
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            val pId = selectedPatient?.id ?: patientId ?: "PATIENT_AITA_BORAH"
                            val newRel = RelationshipEntity(
                                id = UUID.randomUUID().toString(),
                                patientId = pId,
                                name = nameInput.trim(),
                                relationshipType = selectedRelation
                            )
                            relationships.add(newRel)
                            nameInput = ""
                            statusMessage = "Added ${newRel.name} to Care Circle."
                        }
                    },
                    enabled = nameInput.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.MutedHeritageTerracotta)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = AasritiColorTokens.WarmIvory)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add to Care Circle", fontWeight = FontWeight.Bold, color = AasritiColorTokens.WarmIvory)
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Current Care Circle (${relationships.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AasritiColorTokens.DeepCharcoal
                )
            }

            items(relationships) { rel ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AasritiColorTokens.SoftCream),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AasritiColorTokens.WarmSunkenSurface)
                                .border(1.5.dp, AasritiColorTokens.MutedHeritageTerracotta, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = rel.name.firstOrNull()?.toString() ?: "?",
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.MutedHeritageTerracotta,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = rel.name,
                                fontWeight = FontWeight.Bold,
                                color = AasritiColorTokens.DeepCharcoal,
                                fontSize = 16.sp
                            )
                            Text(
                                text = rel.relationshipType.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall,
                                color = AasritiColorTokens.WarmSlate
                            )
                        }
                        IconButton(
                            onClick = {
                                relationships.remove(rel)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = AasritiColorTokens.WarmSlate
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val pId = selectedPatient?.id ?: patientId ?: "PATIENT_AITA_BORAH"
                        coroutineScope.launch {
                            for (rel in relationships) {
                                patientRepository.addRelationship(rel)
                            }
                            onComplete(pId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AasritiColorTokens.DeepNortheastForest)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AasritiColorTokens.WarmIvory)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Complete Setup & Enter Dashboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AasritiColorTokens.WarmIvory
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = {
                        val pId = selectedPatient?.id ?: patientId ?: "PATIENT_AITA_BORAH"
                        onComplete(pId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AasritiColorTokens.WarmStoneBorder)
                ) {
                    Text(
                        text = "Skip for Now",
                        color = AasritiColorTokens.WarmSlate,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
