//
//  ViewController.swift
//  PDFCreator
//
//  Created by xulihang on 2024/6/14.
//

import UIKit
import PhotosUI
import DynamsoftCore
import DynamsoftCaptureVisionRouter
import DynamsoftDocumentNormalizer
import DynamsoftUtility

class ViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate, PHPickerViewControllerDelegate {
    @IBOutlet weak var statusLabel: UILabel!
    @IBOutlet weak var selectImagesUIButton: UIButton!
    @IBOutlet weak var colorModeUIPickerView: UIPickerView!
    @IBOutlet weak var enableAutoCroppingUISwitch: UISwitch!
    let cvr:CaptureVisionRouter = CaptureVisionRouter()
    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)
        DispatchQueue.main.async {
            var images:[UIImage] = []
            var processed = 0
            let size = results.count
            for item in results {
                if (item.itemProvider.canLoadObject(ofClass: UIImage.self)) {
                    item.itemProvider.loadObject(ofClass: UIImage.self) { image , error  in
                        if let error{
                            print(error)
                        }
                        if let selectedImage = image as? UIImage{
                            print(selectedImage.size)
                            images.append(selectedImage)
                            processed = processed + 1
                            if processed == size {
                                self.mergeImagesIntoPDF(images: images)
                            }
                        }
                    }
                }
            }
        }
    }
    
    func mergeImagesIntoPDF(images:[UIImage]) {
        print("mergeImagesIntoPDF")
        var enableAutoCropping = false
        var selectedColorModeIndex = 0
        DispatchQueue.main.sync {
            statusLabel.text = "Processing..."
            
            if enableAutoCroppingUISwitch.isOn {
                enableAutoCropping = true
            }
            selectedColorModeIndex = colorModeUIPickerView.selectedRow(inComponent: 0)
        }
        let templateName:String;
        if enableAutoCropping {
            print("enable auto cropping")
            templateName = PresetTemplate.detectAndNormalizeDocument.rawValue
        }else{
            print("disable auto cropping")
            templateName = PresetTemplate.normalizeDocument.rawValue
        }
        var settings = try? cvr.getSimplifiedSettings(templateName)
        print("selected row")
        print(selectedColorModeIndex)
        if selectedColorModeIndex == 0 {
            settings?.documentSettings?.colourMode = ImageColourMode.binary
        }else if selectedColorModeIndex == 1 {
            settings?.documentSettings?.colourMode = ImageColourMode.grayscale
        }else{
            settings?.documentSettings?.colourMode = ImageColourMode.colour
        }
        if enableAutoCropping == false {
            settings?.roi = Quadrilateral(pointArray: [CGPoint(x:0,y:0),CGPoint(x:100,y:0),CGPoint(x:100,y:100),CGPoint(x:0,y:100)])
            settings?.roiMeasuredInPercentage = true
        }
        try? cvr.updateSettings(templateName, settings: settings!)
        
        let imageManager = ImageManager()
        let url = FileManager.default.temporaryDirectory
                                                        .appendingPathComponent(UUID().uuidString)
                                                        .appendingPathExtension("pdf")
        for image in images {
            let capturedResult:CapturedResult = cvr.captureFromImage(image, templateName: templateName)
            let items = capturedResult.items ?? []
            for item in items {
                if item.type == CapturedResultItemType.normalizedImage {
                    let image:NormalizedImageResultItem = item as! NormalizedImageResultItem
                    try? imageManager.saveToFile(image.imageData!, path: url.path, overWrite: true)
                }
            }
        }
        DispatchQueue.main.async {
            self.statusLabel.text = ""
            let objectsToShare = [url]
            let activityVC = UIActivityViewController(activityItems: objectsToShare, applicationActivities: nil)

            self.present(activityVC, animated: true, completion: nil)
        }
        
    }
    
    
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return 3
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int,
                        forComponent component: Int) -> String? {
        if row == 0 {
            return "Black & White"
        }else if row == 1 {
            return "Grayscale"
        }else{
            return "Color"
        }
                
    }
    
   
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        colorModeUIPickerView.dataSource = self
        colorModeUIPickerView.delegate = self
    }

    @IBAction func selectImagesUIButton_clicked(_ sender: Any) {
        print("clicked")

        var configuration = PHPickerConfiguration(photoLibrary: .shared())
        //0 - unlimited 1 - default
        configuration.selectionLimit = 0
        configuration.filter = .images
        let pickerViewController = PHPickerViewController(configuration: configuration)
        pickerViewController.delegate = self
        present(pickerViewController, animated: true)
    }
}

