package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ChildProfileEntity
import com.example.data.local.RecentActivityEntity
import com.example.ui.components.KidsTopBar
import com.example.ui.theme.BrightOrange
import com.example.ui.theme.BubblegumPink
import com.example.ui.theme.CandyPurple
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkText
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MintGreen
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import com.example.ui.theme.SunshineDark
import com.example.viewmodel.KidsScreen
import com.example.viewmodel.KidsViewModel

@Composable
fun HomeScreen(
    viewModel: KidsViewModel,
    profile: ChildProfileEntity,
    recentActivities: List<RecentActivityEntity>,
    soundEnabled: Boolean
) {
    var showParentGate by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var gateNum1 by remember { mutableStateOf(4) }
    var gateNum2 by remember { mutableStateOf(3) }
    var gateInput by remember { mutableStateOf("") }
    var gateError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF8))
            .testTag("home_screen")
    ) {
        // Top Bar
        KidsTopBar(
            title = "Çocuk Oyunları",
            stars = profile.totalStars,
            soundEnabled = soundEnabled,
            onToggleSound = { viewModel.toggleSound() }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner with Image
            item {
                HeroWelcomeCard(
                    profile = profile,
                    onClaimDaily = { viewModel.claimDailyBonus() }
                )
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🎮 Neşeli Oyunlar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "Birini Seç ve Oyna!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SkyBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Game Cards
            item {
                GameCardItem(
                    title = "Balon Patlatmaca",
                    subtitle = "Renkli balonları yakala, patlat, puan topla!",
                    emoji = "🎈",
                    badge = "Çok Eğlenceli!",
                    gradientColors = listOf(CoralRed, BubblegumPink),
                    testTag = "game_card_balloon",
                    onClick = { viewModel.navigateTo(KidsScreen.BalloonPop) }
                )
            }

            item {
                GameCardItem(
                    title = "Sevimli Hayvan Yapbozu",
                    subtitle = "Parçaları doğru yerlere koy, resmi tamamla!",
                    emoji = "🧩",
                    badge = "YENİ ⭐ Sevimli Yapboz",
                    gradientColors = listOf(CandyPurple, SkyBlue),
                    testTag = "game_card_puzzle",
                    onClick = { viewModel.navigateTo(KidsScreen.Puzzle) }
                )
            }

            item {
                GameCardItem(
                    title = "Yıldız Avcısı Roket",
                    subtitle = "Roketini uçur, gökyüzündeki parlak yıldızları topla!",
                    emoji = "🚀",
                    badge = "YENİ 🌟 Uzay Macerası",
                    gradientColors = listOf(BrightOrange, CoralRed),
                    testTag = "game_card_rocket",
                    onClick = { viewModel.navigateTo(KidsScreen.Rocket) }
                )
            }

            item {
                GameCardItem(
                    title = "Sihirli Piyano & Hayvanlar",
                    subtitle = "Renkli tuşlarla müzik çal, hayvan seslerini keşfet!",
                    emoji = "🎹",
                    badge = "Notalar & Sesler",
                    gradientColors = listOf(SkyBlue, OceanBlue),
                    testTag = "game_card_piano",
                    onClick = { viewModel.navigateTo(KidsScreen.Piano) }
                )
            }

            item {
                GameCardItem(
                    title = "Hayvan Eşleştirmece",
                    subtitle = "Sevimli kart çiftlerini bul, hafızanı test et!",
                    emoji = "🃏",
                    badge = "Zeka Oyunu",
                    gradientColors = listOf(MintGreen, LimeGreen),
                    testTag = "game_card_memory",
                    onClick = { viewModel.navigateTo(KidsScreen.MemoryMatch) }
                )
            }

            item {
                GameCardItem(
                    title = "Sihirli Boyama & Çizim",
                    subtitle = "Rengarenk boya, ışıklı damgalar bas, sanat yap!",
                    emoji = "🎨",
                    badge = "Yaratıcı Dünya",
                    gradientColors = listOf(CandyPurple, BubblegumPink),
                    testTag = "game_card_drawing",
                    onClick = { viewModel.navigateTo(KidsScreen.Drawing) }
                )
            }

            item {
                GameCardItem(
                    title = "Sevimli Dostum Tonton",
                    subtitle = "Ayıcık Tonton'u besle, sev ve çıkartmaları topla!",
                    emoji = "🧸",
                    badge = "Çıkartma Albümü",
                    gradientColors = listOf(SunnyYellow, BrightOrange),
                    testTag = "game_card_pet",
                    onClick = { viewModel.navigateTo(KidsScreen.PetAndAlbum) }
                )
            }

            // Recent Achievements & Stars
            if (recentActivities.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⭐ Son Maceraların",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        fontSize = 18.sp
                    )
                }

                items(recentActivities.take(4)) { activity ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(activity.iconEmoji, fontSize = 26.sp)
                                Column {
                                    Text(
                                        text = activity.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkText
                                    )
                                }
                            }
                            if (activity.starsEarned != 0) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (activity.starsEarned > 0) SunnyYellow.copy(alpha = 0.3f) else CoralRed.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (activity.starsEarned > 0) "+${activity.starsEarned} ⭐" else "${activity.starsEarned} ⭐",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = DarkText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            gateNum1 = (2..7).random()
                            gateNum2 = (1..6).random()
                            gateInput = ""
                            gateError = false
                            showParentGate = true
                        }
                        .testTag("parent_privacy_card"),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SkyBlue.copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Security,
                                        contentDescription = "Güvenlik",
                                        tint = SkyBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Ebeveyn Alanı & Gizlilik",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Text(
                                    text = "%100 Güvenli • Veri Toplanmaz • Reklamsız",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Parental Gate Dialog
    if (showParentGate) {
        Dialog(onDismissRequest = { showParentGate = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CoralRed.copy(alpha = 0.15f),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = CoralRed, modifier = Modifier.size(26.dp))
                        }
                    }

                    Text(
                        text = "Ebeveyn Doğrulaması",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Text(
                        text = "Lütfen devam etmek için yetişkin olduğunuzu doğrulayın:",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF64748B)
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$gateNum1 + $gateNum2 = ?",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            color = DarkText,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = gateInput,
                        onValueChange = {
                            gateInput = it
                            gateError = false
                        },
                        label = { Text("Sonucu yazın") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = gateError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (gateError) {
                        Text(
                            text = "Sonuç hatalı, lütfen tekrar deneyin.",
                            color = CoralRed,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showParentGate = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("İptal")
                        }

                        Button(
                            onClick = {
                                val expected = gateNum1 + gateNum2
                                if (gateInput.trim().toIntOrNull() == expected) {
                                    showParentGate = false
                                    showPrivacyDialog = true
                                } else {
                                    gateError = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Giriş Yap", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Comprehensive Privacy Policy Dialog
    if (showPrivacyDialog) {
        Dialog(onDismissRequest = { showPrivacyDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🛡️", fontSize = 22.sp)
                            Text(
                                text = "Gizlilik & Çocuk Güvenliği",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }
                        IconButton(onClick = { showPrivacyDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF64748B))
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MintGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MintGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✅", fontSize = 20.sp)
                                Text(
                                    text = "Google Play Aileler Politikası & COPPA Uyumlu: Hiçbir kişisel veri toplanmaz.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                            }
                        }

                        Text(
                            text = "1. Kişisel Veri Toplanmaz",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "Bu çocuk oyunu uygulaması; çocukların veya ebeveynlerin adını, e-posta adresini, konum bilgisini, IP adresini, ses kayıtlarını veya cihaz kimliğini ASLA toplamaz, kaydetmez veya internet üzerinden herhangi bir sunucuya iletmez.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569)
                        )

                        Text(
                            text = "2. Çevrimdışı ve Yerel Depolama",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "Oyun içi kazanılan yıldızlar, açılan çıkartmalar ve çizilen resimler yalnızca cihazınızın kendi güvenli yerel veritabanında saklanır. Cihaz dışına hiçbir veri çıkmaz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569)
                        )

                        Text(
                            text = "3. Reklamsız ve Güvenli",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "Uygulamada üçüncü taraf reklam ağları veya uygulama içi satın alma bulunmamaktadır. Çocukların yanlışlıkla harcama yapması veya uygunsuz içeriklerle karşılaşması imkansızdır.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569)
                        )

                        Text(
                            text = "4. İletişim",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "Herhangi bir soru veya geri bildiriminiz için bize destek e-postasından ulaşabilirsiniz: emirq143@gmail.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569)
                        )
                    }

                    Button(
                        onClick = { showPrivacyDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Anladım", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroWelcomeCard(
    profile: ChildProfileEntity,
    onClaimDaily: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce_gift")
    val giftScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gift_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_welcome_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_kids_hero),
                    contentDescription = "Neşeli Oyun Parkı",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0x99000000))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Merhaba Küçük Yıldız! ✨",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
            }

            // Daily Gift Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFFBEB))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .scale(giftScale)
                            .clip(CircleShape)
                            .background(SunnyYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎁", fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = "Günün Yıldız Hediyesi",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "+25 Parlak Yıldız Al",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB45309),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onClaimDaily,
                    colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("claim_daily_gift_btn")
                ) {
                    Text("Topla! ⭐", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun GameCardItem(
    title: String,
    subtitle: String,
    emoji: String,
    badge: String,
    gradientColors: List<Color>,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.4f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = emoji, fontSize = 34.sp)
                        }
                    }

                    Column {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 19.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Oyna",
                            tint = gradientColors.first(),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
