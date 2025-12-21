import SwiftUI
import MobileShared

@main
struct iOSApp: App {
    init() {
        KoinHelperKt.doInitKoin(appDeclaration: { _ in })
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}