package com.najm.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {

    // ===== الألوان =====
    static final int BG = Color.parseColor("#11141D"); // الخلفية الداكنة
    static final int CARD = Color.parseColor("#1E2232"); // لون البطاقات
    static final int PRIMARY = Color.parseColor("#3D63FF"); // الأزرق
    static final int TEXT = Color.WHITE;
    static final int TEXT_DIM = Color.parseColor("#A0A5B5");
    static final int GOLD = Color.parseColor("#FFD700");

    // ===== المتغيرات =====
    SharedPreferences prefs;
    LinearLayout root;
    LinearLayout bottomNav;
    String currentUser = null;
    boolean isOwner = false;
    String currentChatId = null;
    int currentTab = 3; // نبدأ بشاشة الملف الشخصي كما في الصورة

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("najm", Context.MODE_PRIVATE);
        currentUser = prefs.getString("current_user", null);
        isOwner = prefs.getBoolean("is_owner", false);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        setContentView(root);

        if (currentUser == null) {
            showAuth();
        } else {
            showHome();
        }
    }

    // ============================================
    // 🏠 الشاشة الرئيسية + الشريط السفلي
    // ============================================
    void showHome() {
        root.removeAllViews();

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setBackgroundColor(BG);
        LinearLayout.LayoutParams contentLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        root.addView(content, contentLp);

        renderTab(content);

        // الشريط السفلي (4 أزرار)
        bottomNav = new LinearLayout(this);
        bottomNav.setOrientation(LinearLayout.HORIZONTAL);
        bottomNav.setBackgroundColor(BG);
        bottomNav.setPadding(10, 10, 10, 30);
        LinearLayout.LayoutParams navLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        root.addView(bottomNav, navLp);

        addNavItem(0, "💬", "المحادثات");
        addNavItem(1, "👥", "جهات الاتصال");
        addNavItem(2, "⚙️", "الإعدادات");
        addNavItem(3, "👤", "الملف الشخصي");
    }

    void addNavItem(int idx, String icon, String label) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        item.setLayoutParams(lp);

        TextView ic = new TextView(this);
        ic.setText(icon);
        ic.setTextSize(22);
        ic.setGravity(Gravity.CENTER);
        item.addView(ic);

        TextView lbl = new TextView(this);
        lbl.setText(label);
        lbl.setTextSize(12);
        lbl.setTextColor(idx == currentTab ? PRIMARY : TEXT_DIM);
        if (idx == currentTab) lbl.setTypeface(null, Typeface.BOLD);
        lbl.setGravity(Gravity.CENTER);
        item.addView(lbl);

        item.setOnClickListener(v -> {
            currentTab = idx;
            showHome();
        });
        bottomNav.addView(item);
    }

    void renderTab(LinearLayout content) {
        switch (currentTab) {
            case 0: addHeader(content, "المحادثات"); renderChats(content); break;
            case 1: addHeader(content, "جهات الاتصال"); renderContacts(content); break;
            case 2: addHeader(content, "الإعدادات"); renderSettings(content); break;
            case 3: renderProfile(content); break; // شاشة الملف الشخصي
        }
    }

    // ============================================
    // 👤 شاشة الملف الشخصي (مثل الصورة تماماً)
    // ============================================
    void renderProfile(LinearLayout content) {
        ScrollView sv = new ScrollView(this);
        LinearLayout profileLayout = new LinearLayout(this);
        profileLayout.setOrientation(LinearLayout.VERTICAL);
        profileLayout.setPadding(30, 40, 30, 40);

        // 1. الصورة الشخصية
        TextView avatar = new TextView(this);
        avatar.setText("👤");
        avatar.setTextSize(60);
        avatar.setGravity(Gravity.CENTER);
        GradientDrawable avBg = new GradientDrawable();
        avBg.setColor(CARD);
        avBg.setShape(GradientDrawable.OVAL);
        avBg.setStroke(4, PRIMARY); // الإطار
        avatar.setBackground(avBg);
        LinearLayout.LayoutParams avLp = new LinearLayout.LayoutParams(250, 250);
        avLp.gravity = Gravity.CENTER;
        profileLayout.addView(avatar, avLp);

        // 2. الاسم
        TextView name = new TextView(this);
        name.setText(currentUser);
        name.setTextSize(24);
        name.setTextColor(TEXT);
        name.setTypeface(null, Typeface.BOLD);
        name.setGravity(Gravity.CENTER);
        name.setPadding(0, 20, 0, 5);
        profileLayout.addView(name);

        // 3. حالة الاتصال
        TextView status = new TextView(this);
        status.setText("متصل الآن");
        status.setTextSize(14);
        status.setTextColor(TEXT_DIM);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 0, 0, 30);
        profileLayout.addView(status);

        // 4. الأزرار الثلاثة
        LinearLayout buttonsRow = new LinearLayout(this);
        buttonsRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonsRow.setGravity(Gravity.CENTER);
        buttonsRow.addView(makeProfileButton("✏️", "تعديل البيانات"));
        buttonsRow.addView(makeProfileButton("📷", "تعيين صورة"));
        buttonsRow.addView(makeProfileButton("⚙️", "الإعدادات"));
        profileLayout.addView(buttonsRow);

        // 5. بطاقة المعلومات
        LinearLayout infoCard = new LinearLayout(this);
        infoCard.setOrientation(LinearLayout.VERTICAL);
        infoCard.setPadding(30, 30, 30, 30);
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(CARD);
        cardBg.setCornerRadius(30);
        infoCard.setBackground(cardBg);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLp.topMargin = 30;
        cardLp.bottomMargin = 40;
        profileLayout.addView(infoCard, cardLp);

        infoCard.addView(makeInfoRow("📱", "+964 777 000 0000", "جوال"));
        infoCard.addView(makeInfoRow("@", "@" + currentUser, "اسم المستخدم"));
        infoCard.addView(makeInfoRow("🎂", "15 نوفمبر", "يوم الميلاد"));

        // 6. قسم المنشورات
        LinearLayout postsTab = new LinearLayout(this);
        postsTab.setOrientation(LinearLayout.HORIZONTAL);
        postsTab.setGravity(Gravity.CENTER);
        postsTab.setPadding(0, 0, 0, 40);

        TextView activeTab = new TextView(this);
        activeTab.setText("المنشورات");
        activeTab.setTextColor(TEXT);
        activeTab.setPadding(40, 15, 40, 15);
        GradientDrawable activeBg = new GradientDrawable();
        activeBg.setColor(CARD);
        activeBg.setCornerRadius(30);
        activeTab.setBackground(activeBg);

        TextView inactiveTab = new TextView(this);
        inactiveTab.setText("المؤرشفة");
        inactiveTab.setTextColor(TEXT_DIM);
        inactiveTab.setPadding(40, 15, 40, 15);

        postsTab.addView(activeTab);
        postsTab.addView(inactiveTab);
        profileLayout.addView(postsTab);

        // 7. لا توجد منشورات
        TextView emptyIcon = new TextView(this);
        emptyIcon.setText("📝");
        emptyIcon.setTextSize(60);
        emptyIcon.setGravity(Gravity.CENTER);
        emptyIcon.setAlpha(0.3f);
        profileLayout.addView(emptyIcon);

        TextView emptyText = new TextView(this);
        emptyText.setText("لا توجد منشورات بعد...");
        emptyText.setTextSize(18);
        emptyText.setTextColor(TEXT);
        emptyText.setTypeface(null, Typeface.BOLD);
        emptyText.setGravity(Gravity.CENTER);
        emptyText.setPadding(0, 20, 0, 10);
        profileLayout.addView(emptyText);

        TextView emptySub = new TextView(this);
        emptySub.setText("نشر الصور والمقاطع المرئية لعرضها على صفحة ملفك الشخصي");
        emptySub.setTextSize(14);
        emptySub.setTextColor(TEXT_DIM);
        emptySub.setGravity(Gravity.CENTER);
        profileLayout.addView(emptySub);

        // زر إضافة منشور
        Button addPost = new Button(this);
        addPost.setText("📷 إضافة منشور");
        addPost.setTextColor(TEXT);
        addPost.setTextSize(16);
        GradientDrawable addBg = new GradientDrawable();
        addBg.setColor(PRIMARY);
        addBg.setCornerRadius(30);
        addPost.setBackground(addBg);
        LinearLayout.LayoutParams addLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, 120);
        addLp.gravity = Gravity.CENTER;
        addLp.topMargin = 40;
        profileLayout.addView(addPost, addLp);

        sv.addView(profileLayout);
        content.addView(sv, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    View makeProfileButton(String icon, String text) {
        LinearLayout btn = new LinearLayout(this);
        btn.setOrientation(LinearLayout.VERTICAL);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(20, 20, 20, 20);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(CARD);
        bg.setCornerRadius(25);
        btn.setBackground(bg);
        
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        lp.setMargins(10, 0, 10, 0);
        btn.setLayoutParams(lp);

        TextView ic = new TextView(this);
        ic.setText(icon);
        ic.setTextSize(24);
        ic.setGravity(Gravity.CENTER);
        btn.addView(ic);

        TextView lbl = new TextView(this);
        lbl.setText(text);
        lbl.setTextSize(12);
        lbl.setTextColor(TEXT);
        lbl.setGravity(Gravity.CENTER);
        lbl.setPadding(0, 10, 0, 0);
        btn.addView(lbl);

        return btn;
    }

    View makeInfoRow(String icon, String value, String label) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setGravity(Gravity.END); // محاذاة لليمين
        row.setPadding(0, 15, 0, 15);

        TextView val = new TextView(this);
        val.setText(icon + " " + value);
        val.setTextColor(TEXT);
        val.setTextSize(16);
        row.addView(val);

        TextView lbl = new TextView(this);
        lbl.setText(label);
        lbl.setTextColor(TEXT_DIM);
        lbl.setTextSize(12);
        lbl.setPadding(0, 5, 0, 0);
        row.addView(lbl);

        return row;
    }

    // ============================================
    // 💬 شاشة المحادثات
    // ============================================
    void renderChats(LinearLayout content) {
        ScrollView sv = new ScrollView(this);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(20, 0, 20, 20);

        String usersList = prefs.getString("users_list", "");
        List<String> users = new ArrayList<>();
        if (!usersList.isEmpty()) {
            for (String u : usersList.split(",")) {
                if (!u.equals(currentUser)) users.add(u);
            }
        }

        if (users.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("لا توجد محادثات بعد");
            empty.setTextColor(TEXT_DIM);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, 100, 0, 0);
            list.addView(empty);
        } else {
            for (String u : users) {
                LinearLayout item = new LinearLayout(this);
                item.setPadding(20, 20, 20, 20);
                GradientDrawable bg = new GradientDrawable();
                bg.setColor(CARD);
                bg.setCornerRadius(30);
                item.setBackground(bg);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = 15;
                item.setLayoutParams(lp);

                TextView name = new TextView(this);
                name.setText(u);
                name.setTextColor(TEXT);
                name.setTextSize(18);
                item.addView(name);
                
                item.setOnClickListener(v -> showChat(u));
                list.addView(item);
            }
        }
        sv.addView(list);
        content.addView(sv);
    }

    void showChat(String otherUser) {
        root.removeAllViews();
        addHeader(root, "محادثة مع " + otherUser);
        
        // زر رجوع مؤقت
        Button back = new Button(this);
        back.setText("⬅ رجوع");
        back.setOnClickListener(v -> showHome());
        root.addView(back);
    }

    // ============================================
    // ⚙️ شاشة الإعدادات
    // ============================================
    void renderSettings(LinearLayout content) {
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(20, 0, 20, 20);

        // زر المالك
        Button ownerBtn = new Button(this);
        ownerBtn.setText("🛡️ دخول المالك");
        GradientDrawable oBg = new GradientDrawable();
        oBg.setColor(CARD);
        oBg.setCornerRadius(30);
        ownerBtn.setBackground(oBg);
        ownerBtn.setTextColor(GOLD);
        ownerBtn.setOnClickListener(v -> showOwnerLogin());
        list.addView(ownerBtn);

        // زر الخروج
        Button logoutBtn = new Button(this);
        logoutBtn.setText("🚪 تسجيل الخروج");
        GradientDrawable lBg = new GradientDrawable();
        lBg.setColor(Color.RED);
        lBg.setCornerRadius(30);
        logoutBtn.setBackground(lBg);
        logoutBtn.setTextColor(TEXT);
        logoutBtn.setOnClickListener(v -> {
            prefs.edit().remove("current_user").apply();
            currentUser = null;
            isOwner = false;
            showAuth();
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 20;
        list.addView(logoutBtn, lp);

        content.addView(list);
    }

    // ============================================
    // 🔐 شاشة الدخول
    // ============================================
    void showAuth() {
        root.removeAllViews();
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(60, 60, 60, 60);

        EditText userIn = new EditText(this);
        userIn.setHint("اسم المستخدم");
        userIn.setTextColor(TEXT);
        userIn.setHintTextColor(TEXT_DIM);
        GradientDrawable inputBg = new GradientDrawable();
        inputBg.setColor(CARD);
        inputBg.setCornerRadius(20);
        userIn.setBackground(inputBg);
        userIn.setPadding(30, 20, 30, 20);
        box.addView(userIn);

        EditText passIn = new EditText(this);
        passIn.setHint("كلمة المرور");
        passIn.setTextColor(TEXT);
        passIn.setHintTextColor(TEXT_DIM);
        passIn.setBackground(inputBg);
        passIn.setPadding(30, 20, 30, 20);
        passIn.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams passLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        passLp.topMargin = 20;
        box.addView(passIn, passLp);

        Button loginBtn = new Button(this);
        loginBtn.setText("دخول / إنشاء حساب");
        loginBtn.setTextColor(TEXT);
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(PRIMARY);
        btnBg.setCornerRadius(30);
        loginBtn.setBackground(btnBg);
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        btnLp.topMargin = 30;
        box.addView(loginBtn, btnLp);

        loginBtn.setOnClickListener(v -> {
            String u = userIn.getText().toString().trim();
            String p = passIn.getText().toString();
            if (!u.isEmpty() && !p.isEmpty()) {
                prefs.edit()
                    .putString("current_user", u)
                    .putString("user_"+u, p)
                    .putString("users_list", prefs.getString("users_list", "") + "," + u)
                    .apply();
                currentUser = u;
                showHome();
            } else {
                Toast.makeText(this, "أدخل البيانات كاملة", Toast.LENGTH_SHORT).show();
            }
        });

        root.addView(box);
    }

    // ============================================
    // 🛡️ شاشة المالك (0852)
    // ============================================
    void showOwnerLogin() {
        root.removeAllViews();
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(60, 60, 60, 60);

        TextView icon = new TextView(this);
        icon.setText("🛡️");
        icon.setTextSize(80);
        icon.setGravity(Gravity.CENTER);
        box.addView(icon);

        EditText codeIn = new EditText(this);
        codeIn.setHint("••••");
        codeIn.setTextColor(TEXT);
        codeIn.setTextSize(30);
        codeIn.setGravity(Gravity.CENTER);
        codeIn.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        GradientDrawable inputBg = new GradientDrawable();
        inputBg.setColor(CARD);
        inputBg.setCornerRadius(30);
        codeIn.setBackground(inputBg);
        LinearLayout.LayoutParams inLp = new LinearLayout.LayoutParams(400, 150);
        inLp.topMargin = 40;
        box.addView(codeIn, inLp);

        Button claimBtn = new Button(this);
        claimBtn.setText("دخول");
        claimBtn.setTextColor(Color.BLACK);
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(GOLD);
        btnBg.setCornerRadius(30);
        claimBtn.setBackground(btnBg);
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        btnLp.topMargin = 30;
        box.addView(claimBtn, btnLp);

        claimBtn.setOnClickListener(v -> {
            if (codeIn.getText().toString().equals("0852")) {
                if (!prefs.getBoolean("owner_claimed", false) || isOwner) {
                    prefs.edit().putBoolean("owner_claimed", true).putBoolean("is_owner", true).apply();
                    isOwner = true;
                    Toast.makeText(this, "🎉 صرت المالك!", Toast.LENGTH_SHORT).show();
                    showHome();
                } else {
                    Toast.makeText(this, "يوجد مالك بالفعل", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "كود خاطئ", Toast.LENGTH_SHORT).show();
            }
        });

        Button backBtn = new Button(this);
        backBtn.setText("رجوع");
        backBtn.setTextColor(TEXT_DIM);
        GradientDrawable backBg = new GradientDrawable();
        backBg.setColor(Color.TRANSPARENT);
        backBtn.setBackground(backBg);
        backBtn.setOnClickListener(v -> showHome());
        box.addView(backBtn);

        root.addView(box);
    }

    // ============================================
    // 🛠️ دوال مساعدة
    // ============================================
    void addHeader(LinearLayout content, String text) {
        LinearLayout header = new LinearLayout(this);
        header.setBackgroundColor(BG);
        header.setPadding(30, 30, 30, 30);
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(24);
        tv.setTextColor(TEXT);
        tv.setTypeface(null, Typeface.BOLD);
        header.addView(tv);
        content.addView(header);
    }

    void renderContacts(LinearLayout content) {
        renderChats(content); // نفس الكود تقريباً
    }
}