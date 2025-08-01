package com.flutter_inner_printer.b08;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.flutter_inner_printer.b08.utils.FlutterInnerPrinterHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.FlutterPlugin;

public class FlutterInnerPrinterPlugin implements FlutterPlugin, MethodCallHandler {
    private String CHANNEL = "flutter_inner_printer_b08/method_channel";
    String bigFont = "BeVietnamPro-Medium.ttf";
    String bigFontBold = "BeVietnamPro-Bold.ttf";
    String fontExtraBold = "BeVietnamPro-ExtraBold.ttf";
    String fontLight = "BeVietnamPro-Light.ttf";
    String typeFont = "";

    FlutterInnerPrinterHelper printHelper;

    private void checkFont(boolean isBold, boolean isLight, boolean isExtra) {
        if (isExtra) {
            typeFont = fontExtraBold;
        } else if (isLight) {
            typeFont = fontLight;
        } else {
            typeFont = isBold ? bigFontBold : bigFont;
        }

    }

    @Override
    public void onAttachedToEngine(
            @NonNull FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel channel = new MethodChannel(
                flutterPluginBinding.getBinaryMessenger(),
                CHANNEL);
        printHelper = new FlutterInnerPrinterHelper(flutterPluginBinding.getApplicationContext());

        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        // super.configureFlutterEngine(flutterEngine);
        // new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),
        // CHANNEL)
        // .setMethodCallHandler(
        // (call, result) -> {
        switch (call.method) {
            case "BIND_PRINTER_SERVICE":
                printHelper.initSunmiPrinterService();
                result.success(true);
                break;
            case "UNBIND_PRINTER_SERVICE":
                printHelper.deInitSunmiPrinterService();
                result.success(true);
                break;
            case "PRINTER_EXAMPLE":
                printHelper.printExample();
                result.success(true);
                break;
            case "INIT_PRINTER":
                printHelper.initPrinter();
                result.success(true);
                break;
            case "ENTER_PRINTER_BUFFER":
                Boolean clearEnter  = call.argument("clearEnter");
                printHelper.exitPrinterBuffer(clearEnter);
                result.success(true);
                break;
            case "EXIT_PRINTER_BUFFER":
                Boolean clear = call.argument("clearExit");
                printHelper.exitPrinterBuffer(clear);
                result.success(true);
                break;
            case "COMMIT_PRINTER_BUFFER":
                printHelper.commitPrinterBuffer();
                result.success(true);
                break;
            case "FONT_SIZE":
                int fontSize = call.argument("size");
                printHelper.setFontSize(fontSize);
                result.success(true);
                break;
            case "PRINT_TEXT":
                String text = call.argument("text");
                boolean bold = call.argument("bold");
                boolean underLine = call.argument("under_line");
                boolean isLight = call.argument("is_light");
                boolean isExtra = call.argument("is_extra");
                int size = call.argument("size");
                checkFont(bold, isLight, isExtra);
                printHelper.printText(text, size, bold, underLine, typeFont);
                result.success(true);
                break;
            case "PRINT_IMAGE":
                byte[] bytes = call.argument("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                printHelper.printBitmap(bitmap,0);
                result.success(true);

                break;
            case "CUT_PAPER":
                printHelper.cutpaper();
                result.success(true);
                break;
            case "SET_ALIGNMENT":
                int align = call.argument("alignment");
                printHelper.setAlign(align);
                result.success(true);
                break;
            case "LINE_WRAP":
                int lines = call.argument("lines");
                printHelper.printLine(lines);
                result.success(true);
                break;

            case "PRINTER_VERSION":
                String version = printHelper.getPrinterVersion();
                result.success(version);
                break;
            case "PRINTER_SERIALNO":
                String serialNo = printHelper.getPrinterSerialNo();
                result.success(serialNo);
                break;
            case "PRINT_BARCODE":
                String dataBarCode = call.argument("data");
                int symbology = call.argument("symbology");
                int height = call.argument("height");
                int width = call.argument("width");
                int textposition = call.argument("textposition");
                Log.d("PRINT_BARCODE",
                        dataBarCode + symbology + height + width + textposition + "");
                printHelper.printBarCode(dataBarCode, symbology, height, width, textposition);
                result.success(true);
                break;
            case "PRINT_QRCODE":
                String dataQRCode = call.argument("data");
                int modulesize = call.argument("modulesize");
                int errorlevel = call.argument("errorlevel");
                printHelper.printQr(dataQRCode, modulesize, errorlevel);
                result.success(true);
                break;
            case "PRINT_TABLE":
                String colsStr = call.argument("cols");
                int fontSizeT = call.argument("size");
                try {
                    JSONArray cols = new JSONArray(colsStr);
                    String[] colsText = new String[cols.length()];
                    int[] colsWidth = new int[cols.length()];
                    int[] colsAlign = new int[cols.length()];
                    for (int i = 0; i < cols.length(); i++) {
                        JSONObject col = cols.getJSONObject(i);
                        String textColumn = col.getString("text");
                        int widthColumn = col.getInt("width");
                        int alignColumn = col.getInt("align");
                        colsText[i] = textColumn;
                        colsWidth[i] = widthColumn;
                        colsAlign[i] = alignColumn;
                    }

                    printHelper.printTable(colsText, colsWidth, colsAlign, fontSizeT);
                    result.success(true);
                } catch (Exception err) {
                    Log.d("SunmiPrinter", err.getMessage());
                }
                break;
//            case "PRINT_BITMAP":
//                Bitmap bitmap = call.argument("bitmap");
//                int orientation = call.argument("orientation");
//                printHelper.printBitmap(bitmap, orientation);
//                result.success(true);
//                break;
            case "PRINT_STATUS":
                printHelper.showPrinterStatus();
                result.success(true);
                break;
            case "OPEN_CASH_BOX":
                printHelper.openCashBox();
                result.success(true);
                break;
            case "SENT_RAW_DATA":
                byte[] dataRaw = call.argument("data");
                printHelper.sendRawData(dataRaw);
                result.success(true);
                break;
            case "DEVICE_MODEL":
                String deviceModel = printHelper.getDeviceModel();
                result.success(deviceModel);
                break;
            case "PRINT_PAPER":
                String getPrintPaper = printHelper.getPrinterPaper();
                result.success(getPrintPaper);
                break;
            case "FEED_PAPER":
                printHelper.feedPaper();
                result.success(true);
                break;
            case "BACK_LABEL_MODE":
                boolean isBackkLabel = printHelper.isBlackLabelMode();
                result.success(isBackkLabel);
                break;
            case "LABEL_MODEL":
                boolean isLabelMode = printHelper.isLabelMode();
                result.success(isLabelMode);
                break;
            case "PRINT_TRANS":
                printHelper.printTrans(null);
                result.success(true);
                break;
            case "CONTROL_LCD":
                int flag = call.argument("flag");
                printHelper.controlLcd(flag);
                result.success(true);
                break;
            case "SEND_TEXT_TOLCD":
                printHelper.sendTextToLcd();
                result.success(true);
                break;
            case "SEND_TEXTS_TOLCD":
                printHelper.sendTextsToLcd();
                result.success(true);
                break;
            case "SEND_PIC_TOLCD":
                Bitmap pic = call.argument("pic");
                printHelper.sendPicToLcd(pic);
                result.success(true);
                break;
            case "PRINT_ONE_LABEL":
                printHelper.printOneLabel();
                result.success(true);
                break;
            case "PRINT_MULTILABEL":
                int num = call.argument("num");
                printHelper.printMultiLabel(num);
                result.success(true);
                break;
            case "PRINTE_HEAD":
                printHelper.getPrinterHead(null);
                result.success(true);
                break;
            case "PRINTE_DISTANCE":
                printHelper.getPrinterDistance(null);
                result.success(true);
                break;

        }
        // });
    }
}
