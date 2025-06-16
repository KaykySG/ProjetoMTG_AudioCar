import { bootstrapApplication } from "@angular/platform-browser"
import { appConfig } from "./app/app.config"
import { AppComponent } from "./app/app.component"
import { registerLocaleData } from "@angular/common"
import localePt from "@angular/common/locales/pt"

// Registrar os dados de localização para português brasileiro
registerLocaleData(localePt, "pt-BR")

bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err))
