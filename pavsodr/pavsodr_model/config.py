from detectron2.config import CfgNode as CN


def add_model_config(cfg):
    # video data
    # DataLoader
    cfg.INPUT.SAMPLING_FRAME_NUM = 2
    cfg.INPUT.SAMPLING_FRAME_RANGE = 5
    cfg.INPUT.SAMPLING_FRAME_SHUFFLE = False
    cfg.INPUT.AUGMENTATIONS = [] # "brightness", "contrast", "saturation", "rotation"

def add_model_config_soundnet(cfg):
    # video data
    # DataLoader
    cfg.INPUT.SAMPLING_FRAME_NUM = 2
    cfg.INPUT.SAMPLING_FRAME_RANGE = 5
    cfg.INPUT.SAMPLING_FRAME_SHUFFLE = False
    cfg.INPUT.AUGMENTATIONS = [] # "brightness", "contrast", "saturation", "rotation"

    # Audio Encoder
    cfg.AUDIO_ENCODER_NAME = "soundnet"

    # Ablation study configs
    cfg.MODEL.PIXEL_DECODER = CN()
    cfg.MODEL.PIXEL_DECODER.USE_DPD = True
    cfg.MODEL.PIXEL_DECODER.USE_AUDIO = True
    cfg.MODEL.PIXEL_DECODER.USE_AV_SAM = True
    cfg.MODEL.PIXEL_DECODER.USE_AV_IAM = True
    cfg.MODEL.MASK_FORMER.USE_STOD = True
