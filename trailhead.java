package trailhead;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.List;
public class trailhead {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver",
            "C:\\\\Users\\\\hi\\\\Downloads\\\\chromedriver-win64\\\\chromedriver-win64\\\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        driver.get("https://www.salesforce.com/trailblazer/sanjanakoudagani"); // <-- use correct profile URL
        Thread.sleep(15000); // Wait for initial load

        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Keep clicking "Show More" until it's no longer visible
        for (int i = 0; i < 20; i++) { // safeguard limit
            try {
                WebElement showMoreButton = (WebElement) js.executeScript(
                    "return (function findButton(node) {\n" +
                    "  if(!node) return null;\n" +
                    "  if(node.shadowRoot){\n" +
                    "    const btn = node.shadowRoot.querySelector('lwc-tds-button.action.button span.title');\n" +
                    "    if(btn && btn.innerText.trim() === 'Show More') return btn.closest('lwc-tds-button');\n" +
                    "    const children = node.shadowRoot.querySelectorAll('*');\n" +
                    "    for(const child of children){\n" +
                    "      const found = findButton(child);\n" +
                    "      if(found) return found;\n" +
                    "    }\n" +
                    "  }\n" +
                    "  return null;\n" +
                    "})(document.querySelector('tbme-profile-badges'))"
                );

                if (showMoreButton != null) {
                    js.executeScript("arguments[0].click();", showMoreButton);
                    System.out.println("🔁 Clicked 'Show More'");
                    Thread.sleep(5000); // wait for badges to load
                } else {
                    break; // no more button
                }
            } catch (Exception e) {
                break;
            }
        }

        // Now collect all badges (your recursive script)
        String script =
            "const badges = [];\n" +
            "function walk(node){\n" +
            "  if(!node) return;\n" +
            "  const children = node.childNodes || [];\n" +
            "  for(let i=0;i<children.length;i++){ walk(children[i]); }\n" +
            "  if(node.shadowRoot){ const schildren = node.shadowRoot.childNodes || []; for(let j=0;j<schildren.length;j++){ walk(schildren[j]); } }\n" +
            "  if(node.nodeType===1){\n" +
            "    try{\n" +
            "      const tag = node.tagName ? node.tagName.toLowerCase() : '';\n" +
            "      if(tag === 'lwc-tbui-badge'){\n" +
            "        const sr = node.shadowRoot;\n" +
            "        if(sr){\n" +
            "          const a = sr.querySelector('article figure figcaption a');\n" +
            "          if(a && a.innerText && a.innerText.trim().length>0) badges.push(a.innerText.trim());\n" +
            "        }\n" +
            "      }\n" +
            "    } catch(e) {}\n" +
            "  }\n" +
            "}\n" +
            "walk(document);\n" +
            "return badges;";

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) js.executeScript(script);
        System.out.println("🔹 Total badges found: " + result.size());
        for (String b : result) {
            System.out.println("🏅 " + b);
        }
        driver.quit();
    }
}
