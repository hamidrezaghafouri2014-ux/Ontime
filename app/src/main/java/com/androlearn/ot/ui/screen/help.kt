package com.androlearn.ot.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.androlearn.ot.ui.theme.faFont

@Composable
fun Help(navController: NavHostController, paddingValues: PaddingValues) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(30.dp)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            Text(
                text = "راهنمای استفاده از OnTime\n" +
                        "⏰ OnTime چیست؟\n" +
                        "\n" +
                        "OnTime به شما کمک می\u200Cکند برنامه\u200Cهای روزانه\u200Cتان را تنظیم کنید و مهم\u200Cتر از آن، به انجامشان پایبند بمانید.\n" +
                        "\n" +
                        "کافی است برنامه\u200Cهای خود را همراه با روز و ساعت مشخص کنید. OnTime در زمان مناسب به شما یادآوری می\u200Cکند و با پیگیری برنامه\u200Cها، کمک می\u200Cکند کارهایتان را فراموش نکنید.\n" +
                        "\n" +
                        "➕ چطور برنامه جدید اضافه کنم؟\n" +
                        "\n" +
                        "از صفحه اصلی روی دکمه «+» بزنید.\n" +
                        "\n" +
                        "سپس برای برنامه خود:\n" +
                        "\n" +
                        "یک موضوع وارد کنید.\n" +
                        "یک رنگ انتخاب کنید.\n" +
                        "سؤال یادآوری موردنظر خود را بنویسید.\n" +
                        "روزهای هفته\u200Cای که می\u200Cخواهید برنامه در آن\u200Cها اجرا شود را انتخاب کنید.\n" +
                        "ساعت شروع و پایان را مشخص کنید.\n" +
                        "روی «ذخیره» بزنید.\n" +
                        "\n" +
                        "برنامه شما از این پس در زمان مشخص\u200Cشده در برنامه روزانه نمایش داده می\u200Cشود.\n" +
                        "\n" +
                        "\uD83D\uDD14 چطور یادآوری تنظیم کنم؟\n" +
                        "\n" +
                        "OnTime در زمان شروع برنامه به شما Notification می\u200Cدهد تا بدانید زمان انجام آن کار رسیده است.\n" +
                        "\n" +
                        "اما کار OnTime اینجا تمام نمی\u200Cشود!\n" +
                        "\n" +
                        "بعد از مدتی دوباره از شما می\u200Cپرسد:\n" +
                        "\n" +
                        "«کارتو انجام دادی؟»\n" +
                        "\n" +
                        "این پیگیری باعث می\u200Cشود برنامه\u200Cهایتان فقط در یک لیست باقی نمانند و بیشتر به سمت انجام واقعی آن\u200Cها بروید.\n" +
                        "\n" +
                        "\uD83D\uDD04 اگر برنامه\u200Cام را انجام ندادم چه می\u200Cشود؟\n" +
                        "\n" +
                        "نگران نباشید.\n" +
                        "\n" +
                        "اگر در زمان تعیین\u200Cشده نتوانستید برنامه\u200Cتان را انجام دهید، OnTime می\u200Cتواند زمان برنامه را به\u200Cصورت خودکار تغییر دهد تا بتوانید آن را در زمان دیگری انجام دهید.\n" +
                        "\n" +
                        "بنابراین اگر برنامه روزتان طبق چیزی که از قبل تصور کرده بودید پیش نرفت، لازم نیست همه\u200Cچیز را از ابتدا تنظیم کنید.\n" +
                        "\n" +
                        "OnTime با شما سازگار می\u200Cشود؛ نه اینکه شما مجبور باشید همیشه با برنامه سازگار باشید.\n" +
                        "\n" +
                        "✏\uFE0F چطور یک برنامه را ویرایش کنم؟\n" +
                        "\n" +
                        "در بخش «برنامه\u200Cها» برنامه موردنظر خود را انتخاب کنید و گزینه ویرایش را بزنید.\n" +
                        "\n" +
                        "می\u200Cتوانید اطلاعاتی مثل:\n" +
                        "\n" +
                        "موضوع\n" +
                        "رنگ\n" +
                        "روزهای هفته\n" +
                        "ساعت شروع\n" +
                        "ساعت پایان\n" +
                        "سؤال یادآوری\n" +
                        "\n" +
                        "را تغییر دهید.\n" +
                        "\n" +
                        "\uD83D\uDDD1\uFE0F چطور یک برنامه را حذف کنم؟\n" +
                        "\n" +
                        "اگر دیگر به برنامه\u200Cای نیاز ندارید، وارد بخش ویرایش آن شوید و گزینه حذف برنامه را انتخاب کنید.\n" +
                        "\n" +
                        "با حذف برنامه، دیگر برای آن برنامه یادآوری دریافت نخواهید کرد.\n" +
                        "\n" +
                        "\uD83C\uDF19 چطور حالت برنامه را تغییر دهم؟\n" +
                        "\n" +
                        "OnTime از حالت روشن و تاریک پشتیبانی می\u200Cکند.\n" +
                        "\n" +
                        "از بخش تنظیمات می\u200Cتوانید ظاهر موردنظر خود را انتخاب کنید.\n" +
                        "\n" +
                        "حالت تاریک برای استفاده در محیط\u200Cهای کم\u200Cنور مناسب\u200Cتر است و حالت روشن نیز محیطی روشن\u200Cتر و ساده\u200Cتر در اختیار شما قرار می\u200Cدهد.\n" +
                        "\n" +
                        "\uD83D\uDCC5 برنامه روزانه را چطور ببینم؟\n" +
                        "\n" +
                        "در صفحه خانه می\u200Cتوانید برنامه\u200Cهای همان روز را در قالب یک جدول زمانی مشاهده کنید.\n" +
                        "\n" +
                        "هر برنامه با رنگی که برای آن انتخاب کرده\u200Cاید نمایش داده می\u200Cشود تا بتوانید در یک نگاه متوجه شوید:\n" +
                        "\n" +
                        "چه کاری → در چه ساعتی → باید انجام شود.\n" +
                        "\n" +
                        "⭐ نکته مهم\n" +
                        "\n" +
                        "برای اینکه OnTime بهترین عملکرد را داشته باشد، برنامه\u200Cهای واقعی و قابل انجام برای خودتان تنظیم کنید.\n" +
                        "\n" +
                        "به جای اینکه برای یک روز ۲۰ برنامه بنویسید، برنامه\u200Cای تنظیم کنید که واقعاً بتوانید انجام دهید.\n" +
                        "\n" +
                        "OnTime قرار نیست فقط یک لیست پر از کار برای شما بسازد؛\n" +
                        "\n" +
                        "قرار است کمک کند کارهایی را که برای خودتان برنامه\u200Cریزی کرده\u200Cاید، واقعاً انجام دهید.",
                textAlign = TextAlign.Right,
                fontFamily = faFont,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
    }
}