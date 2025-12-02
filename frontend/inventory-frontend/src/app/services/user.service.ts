import { Injectable, signal } from '@angular/core';

type AppLanguage = 'es' | 'en';
type AppTheme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class UserService {
    private readonly storageKey = 'app_user_prefs_v1';

    userName = signal<string>('Invitado');
    companyName = signal<string>('Granotec');
    language = signal<AppLanguage>('es');
    theme = signal<AppTheme>('light');

    constructor() {
        this.restoreFromStorage();
        this.applyThemeClass(this.theme());
    }

    setUser(name: string, company: string) {
        this.userName.set(name);
        this.companyName.set(company);
        this.persist();
    }

    setLanguage(lang: AppLanguage) {
        this.language.set(lang);
        this.persist();
    }

    setTheme(theme: AppTheme) {
        this.theme.set(theme);
        this.applyThemeClass(theme);
        this.persist();
    }

    toggleTheme() {
        const next: AppTheme = this.theme() === 'light' ? 'dark' : 'light';
        this.theme.set(next);
        this.applyThemeClass(next);
        this.persist();
    }

    private applyThemeClass(theme: AppTheme) {
        const root = document.documentElement;
        root.classList.remove('theme-light', 'theme-dark');
        root.classList.add(theme === 'light' ? 'theme-light' : 'theme-dark');
    }

    private restoreFromStorage() {
        try {
            const raw = localStorage.getItem(this.storageKey);
            if (!raw) return;
            const parsed = JSON.parse(raw) as {
                userName?: string;
                companyName?: string;
                language?: AppLanguage;
                theme?: AppTheme;
            };
            if (parsed.userName) this.userName.set(parsed.userName);
            if (parsed.companyName) this.companyName.set(parsed.companyName);
            if (parsed.language) this.language.set(parsed.language);
            if (parsed.theme) this.theme.set(parsed.theme);
        } catch {
            // ignore corrupted storage
        }
    }

    private persist() {
        const payload = {
            userName: this.userName(),
            companyName: this.companyName(),
            language: this.language(),
            theme: this.theme(),
        };
        try {
            localStorage.setItem(this.storageKey, JSON.stringify(payload));
        } catch {
            // storage may be unavailable; fail silently
        }
    }
}


